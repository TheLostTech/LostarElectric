#!/usr/bin/env python3
"""Audit the static site's crawlable source. No ranking or rich-result claims.
Run: python3 scripts/seo-audit.py [--output docs/seo/site-audit-after.json]
"""
import argparse
from collections import Counter
from datetime import datetime, timezone
from html.parser import HTMLParser
import json
from pathlib import Path
import struct
from urllib.parse import unquote, urljoin, urlsplit
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
SITE = ROOT / 'site'
BASE = 'https://lostartechnology.com'

class Page(HTMLParser):
    def __init__(self, path):
        super().__init__(convert_charrefs=True)
        self.path = path
        rel = path.relative_to(SITE).as_posix()
        self.url = BASE + ('/' if rel == 'index.html' else '/' + rel[:-5])
        self.meta, self.canonicals, self.ids, self.refs = {}, [], [], []
        self.headings, self.jsonld, self.images, self.title = [], [], [], ''
        self.capture, self.text, self.heading = None, [], None
        self.feed(path.read_text())
    def handle_starttag(self, tag, attrs):
        a = dict(attrs)
        if a.get('id'): self.ids.append(a['id'])
        if tag == 'meta': self.meta.setdefault(a.get('name', a.get('property', '')), []).append(a.get('content', ''))
        if tag == 'link' and a.get('rel') == 'canonical': self.canonicals.append(a.get('href'))
        if tag in ['a','link','img','script','source']:
            for k in ['href','src']:
                if a.get(k): self.refs.append((tag, a[k]))
        if tag == 'img': self.images.append(a)
        if tag in ['h1','h2','h3','h4','h5','h6']:
            self.heading = {'level':int(tag[1]), 'text':''}
        if tag == 'br' and self.heading is not None: self.heading['text'] += ' '
        if tag == 'title' or (tag == 'script' and a.get('type') == 'application/ld+json'):
            self.capture, self.text = tag, []
    def handle_data(self, data):
        if self.capture: self.text.append(data)
        if self.heading is not None: self.heading['text'] += data
    def handle_endtag(self, tag):
        if self.heading is not None and tag == 'h'+str(self.heading['level']):
            self.heading['text'] = ' '.join(self.heading['text'].split())
            self.headings.append(self.heading)
            self.heading = None
        if tag == self.capture:
            if tag == 'title': self.title = ''.join(self.text).strip()
            else: self.jsonld.append(''.join(self.text))
            self.capture = None
    def one(self,key):
        return self.meta.get(key,[''])[0]

def nodes(value):
    if isinstance(value, dict):
        yield value
        for child in value.values(): yield from nodes(child)
    elif isinstance(value, list):
        for child in value: yield from nodes(child)

def local_path(url):
    part = urlsplit(url)
    if part.netloc != urlsplit(BASE).netloc: return None
    path = SITE / unquote(part.path).lstrip('/')
    if path == SITE: return SITE/'index.html'
    if not path.suffix and path.with_suffix('.html').is_file(): return path.with_suffix('.html')
    return path

def audit():
    pages = [Page(p) for p in sorted(SITE.rglob('*.html'))]
    by_path = {p.path:p for p in pages}
    titles, descriptions = Counter(p.title for p in pages), Counter(p.one('description') for p in pages)
    records = []
    required = ['description','robots','og:title','og:description','og:url','og:type','og:site_name','og:locale','og:image','og:image:width','og:image:height','og:image:alt','twitter:card','twitter:title','twitter:description','twitter:image','twitter:image:alt']
    for p in pages:
        errors, warnings, internal = [], [], 0
        if p.canonicals != [p.url]: errors.append('canonical mismatch or duplicate')
        if not p.title or titles[p.title] != 1: errors.append('missing or duplicate title')
        if descriptions[p.one('description')] != 1: errors.append('duplicate description')
        for key in required:
            if len(p.meta.get(key, [])) != 1 or not p.one(key): errors.append('missing/duplicate metadata: '+key)
        if 'noindex' in p.one('robots').lower(): errors.append('noindex')
        if p.one('og:url') != p.url: errors.append('og:url mismatch')
        if sum(h['level']==1 for h in p.headings)!=1: errors.append('H1 count is not one')
        last = 0
        for h in p.headings:
            if not h['text']: errors.append('empty heading')
            if h['level'] > last+1: warnings.append('heading level jump: '+h['text'])
            last=h['level']
            if '/articoli/' in p.url and h['level']>1 and 'torino' in h['text'].lower(): errors.append('Torino in article subheading')
        if len(p.ids) != len(set(p.ids)): errors.append('duplicate IDs')
        if any('alt' not in img for img in p.images): errors.append('image without alt')
        graph=[]
        for raw in p.jsonld:
            try: graph += list(nodes(json.loads(raw)))
            except json.JSONDecodeError: errors.append('invalid JSON-LD')
        types=sorted({n['@type'] for n in graph if isinstance(n.get('@type'),str)})
        if not {'Organization','WebSite'} <= set(types): errors.append('missing identity schema')
        declared={n['@id'] for n in graph if '@id' in n and len(n)>1}
        for n in graph:
            if set(n)=={'@id'} and n['@id'].startswith(BASE) and n['@id'] not in declared:
                # Canonical WebPage references may point to the linked page.
                target=local_path(n['@id'])
                if not target or not target.is_file(): errors.append('unresolved schema reference: '+n['@id'])
        for tag,ref in p.refs:
            url=urljoin(p.url,ref)
            target=local_path(url)
            if target is None: continue
            internal+=1
            if not target.is_file(): errors.append('missing internal target: '+ref)
            elif target in by_path and urlsplit(url).fragment and unquote(urlsplit(url).fragment) not in by_path[target].ids:
                errors.append('missing fragment: '+ref)
            if tag=='a' and urlsplit(url).path.endswith('.html'): errors.append('internal HTML redirect link: '+ref)
        for key in ['og:image','twitter:image']:
            target=local_path(p.one(key))
            if target is None or not target.is_file(): errors.append('missing social image: '+key)
            elif target.suffix=='.png':
                width,height=struct.unpack('>II',target.read_bytes()[16:24])
                if key=='og:image' and [str(width),str(height)] != [p.one('og:image:width'),p.one('og:image:height')]: errors.append('social image dimensions mismatch')
        records.append({'path':p.path.relative_to(ROOT).as_posix(),'url':p.url,'title':p.title,'title_length':len(p.title),'description_length':len(p.one('description')),'h1':[h['text'] for h in p.headings if h['level']==1],'schema_types':types,'internal_targets_checked':internal,'errors':errors,'warnings':warnings})
    locs=[e.text for e in ET.parse(SITE/'sitemap.xml').findall('.//{http://www.sitemaps.org/schemas/sitemap/0.9}loc')]
    site_errors=[]
    if set(locs)!={p.url for p in pages} or len(locs)!=len(pages): site_errors.append('sitemap/page set mismatch')
    if 'Sitemap: '+BASE+'/sitemap.xml' not in (SITE/'robots.txt').read_text(): site_errors.append('robots sitemap missing')
    return {'checked_at':datetime.now(timezone.utc).isoformat(),'boundary':'Static source, links and metadata; not indexing, ranking, rich-result eligibility or AI citation measurement.','pages':records,'site_errors':site_errors,'summary':{'pages':len(pages),'errors':sum(len(p['errors']) for p in records)+len(site_errors),'warnings':sum(len(p['warnings']) for p in records),'internal_targets_checked':sum(p['internal_targets_checked'] for p in records)}}

if __name__=='__main__':
    parser=argparse.ArgumentParser()
    parser.add_argument('--output',type=Path)
    args=parser.parse_args()
    result=audit()
    if args.output: args.output.write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n')
    print(json.dumps(result,ensure_ascii=False,indent=2))
    raise SystemExit(bool(result['summary']['errors']))
