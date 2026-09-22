/** Public fictional demo. Never imports house snapshots, API runtime or personal configuration. */
import { readFile, writeFile, mkdir } from 'node:fs/promises';
import { createRequire } from 'node:module';
import { dirname, resolve } from 'node:path';
import { fileURLToPath, pathToFileURL } from 'node:url';
import { execFileSync } from 'node:child_process';
const root = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const app = resolve(root, '../Sistema operativo casa');
const output = resolve(root, 'site/assets/house-demo');
const req = createRequire(resolve(app, 'package.json'));
const { renderToStaticMarkup } = req('react-dom/server');
const { createElement } = req('react');
const { PDFDocument } = req('pdf-lib');
const { PLAN_LAYERS, PLAN_OBJECT_TYPES, houseFabric, wallPlanSegments, puntiRisolti, eVerticale } = await import(pathToFileURL(resolve(app, 'core/domain/dist/index.js')).href);
const { buildLayerPdfs, zipLayerPdfs } = await import(pathToFileURL(resolve(app, 'apps/web/src/export-config.ts')).href);
const { buildQuantitySchedule } = await import(pathToFileURL(resolve(app, 'apps/web/src/export-config-quantities.ts')).href);
const { planSymbolLayers, planSymbolViewport } = await import(pathToFileURL(resolve(app, 'apps/web/src/PlanSymbols.tsx')).href);
const catalog = new Map(PLAN_OBJECT_TYPES.map((type:any) => [type.id, type]));
const objects:any[] = [];
function object(typeId:string, x:number,y:number,w?:number,h?:number,rotation=0,label?:string) {
  const type:any = catalog.get(typeId);
  if (!type) throw new Error(`Unknown symbol ${typeId}`);
  const item = { id:`demo-object-${String(objects.length+1).padStart(2,'0')}`, typeId, x,y,w:w??type.defaultW,h:h??type.defaultH,rotation,layer:type.layer, ...(label?{label}:{}) };
  objects.push(item); return item;
}
const room=(slug:string,name:string,kind:string,x:number,y:number,w:number,h:number)=>({slug,name,kind,aliases:[],powerW:0,deviceCount:0,layout:{x,y,w,h,edges:['muro','muro','muro','muro']}});
const rooms=[room('soggiorno','Soggiorno e cucina','living',0,0,5,6),room('camera','Camera','bedroom',5,0,3,3.6),room('bagno','Bagno','bathroom',5,3.6,3,2.4)];
// The openings share the same geometry and wall cuts as House Core.
object('portoncino',3.85,5.91,0.9,0.18,0,'Ingresso');
object('porta',4.6,2.91,0.8,0.18,90,'Porta camera');
object('porta',4.6,4.61,0.8,0.18,90,'Porta bagno');
object('finestra',0.65,-0.09,1.6,0.18,0,'Finestra soggiorno');
object('finestra',6.05,-0.09,1.2,0.18,0,'Finestra camera');
object('finestra',7.64,4.02,0.72,0.18,90,'Finestra bagno');
object('lavello',0.3,0.25,0.9,0.6);
object('modulo_60',1.2,0.25);
object('piano_cottura',1.8,0.25,0.9,0.6);
object('modulo_60',2.7,0.25);
object('frigorifero',3.3,0.25,0.65,0.65);
object('tavolo',2.8,1.5,1.4,0.8);
object('sedia',3.03,0.98,0.4,0.4);
object('sedia',3.58,0.98,0.4,0.4);
object('sedia',3.03,2.42,0.4,0.4,180);
object('sedia',3.58,2.42,0.4,0.4,180);
object('divano',0.4,3.35,2.4,0.95);
object('tavolino',1.05,4.65,1.1,0.6);
object('tv',0.95,5.72,1.4,0.1);
object('letto_matrimoniale',5.65,0.22,1.7,2);
object('comodino',5.2,0.38,0.35,0.35);
object('comodino',7.45,0.38,0.35,0.35);
object('armadio',6.05,2.86,1.65,0.55);
object('lavabo',5.38,3.84,0.8,0.45);
object('wc',7.2,3.85,0.4,0.6);
object('doccia',6.72,4.78,1.05,1.05);
object('quadro',4.2,5.56,0.55,0.16,0,'Quadro Q1');
// Three catalogue strips with declared lengths: 4 + 3 + 2 = 9 m.
object('striscia_led_120',0.4,3.03,4,0.06,0,'LED soggiorno - 4 m');
object('striscia_led_90',0.38,1.08,3,0.06,0,'LED cucina - 3 m');
object('striscia_led_60',5.5,2.53,2,0.06,0,'LED camera - 2 m');
object('plafoniera',5.99,4.66,0.3,0.3,0,'Luce bagno');
const point=(x:number,y:number,quotaM:number)=>({x,y,quotaM});
function route(id:string,label:string,points:any[]){return {id,typeId:'linea_luce',label,layer:'linee-luce',sezione:{forma:'tonda',diametroMm:20},punti:points,pose:points.slice(1).map((b,i)=>b.x===points[i].x&&b.y===points[i].y?'parete':'controsoffitto')}}
const tracciati=[
 route('demo-route-1','Q1 - luci soggiorno',[point(4.47,5.64,1.4),point(4.47,5.64,2.6),point(4.47,3.06,2.6),point(4.4,3.06,2.6)]),
 route('demo-route-2','Derivazione cucina',[point(4.47,3.06,2.6),point(4.47,1.11,2.6),point(3.38,1.11,2.6)]),
 route('demo-route-3','Derivazione camera',[point(4.47,3.06,2.6),point(5.25,3.06,2.6),point(5.25,2.56,2.6),point(5.5,2.56,2.6)]),
 route('demo-route-4','Derivazione bagno',[point(4.47,5.3,2.6),point(6.14,5.3,2.6),point(6.14,4.81,2.6),point(6.14,4.81,2.5)]),
];
const snapshot={house:{house:'Casa esempio · progetto dimostrativo',timezone:'Europe/Rome',heightM:2.7,powerW:0,rooms},objects,catalog:PLAN_OBJECT_TYPES,layers:PLAN_LAYERS,tracciati,exportedAt:'2026-09-23T10:00:00.000Z'};
await mkdir(output,{recursive:true});
const fonts={regular:new Uint8Array(await readFile(resolve(app,'node_modules/pdfjs-dist/standard_fonts/LiberationSans-Regular.ttf'))),bold:new Uint8Array(await readFile(resolve(app,'node_modules/pdfjs-dist/standard_fonts/LiberationSans-Bold.ttf')))};
const files=await buildLayerPdfs(snapshot,fonts,undefined,undefined,{includeGeneral:true,layerIds:['arredi','luci','linee-luce']});
for(const file of files) await writeFile(resolve(output,file.name),file.bytes);
await writeFile(resolve(root,'site/downloads/house-core-esempio.zip'),zipLayerPdfs(files));
const quantities=buildQuantitySchedule(snapshot);
if(quantities.totals.areaM2!==48 || quantities.totals.ledMetres!==9 || objects.filter(o=>catalog.get(o.typeId)?.ledPerMetro).length!==3) throw new Error('Demo totals mismatch');
const S=66,OX=96,OY=80;
const n=(v:number)=>Number(v.toFixed(3));
const X=(v:number)=>n(OX+v*S), Y=(v:number)=>n(OY+v*S);
const escape=(s:string)=>s.replaceAll('&','&amp;').replaceAll('<','&lt;').replaceAll('"','&quot;');
function svgObject(o:any){
 const parts=planSymbolLayers(o.typeId,o.vie).map((layer:any)=>{
   const v=planSymbolViewport(layer.kind,o.w*S,o.h*S);
   const symbol=renderToStaticMarkup(createElement('g',{},layer.elements));
   if(!symbol || symbol.includes('undefined') || symbol.includes('NaN')) throw new Error('Broken shared symbol');
   return `<g data-symbol-layer="${layer.kind}" transform="translate(${n(v.x)} ${n(v.y)}) scale(${n(v.w/100)} ${n(v.h/100)})">${symbol}</g>`;
 }).join('');
 return `<g data-object-id="${o.id}" data-type-id="${o.typeId}" transform="translate(${X(o.x)} ${Y(o.y)}) rotate(${o.rotation||0} ${n(o.w*S/2)} ${n(o.h*S/2)})"><title>${escape(o.label||catalog.get(o.typeId).label)}</title>${parts}</g>`;
}
const fabric=houseFabric(rooms,objects,PLAN_OBJECT_TYPES,{heightM:2.7});
const wallLines=new Set<string>();
for(const wall of fabric.solids.filter((s:any)=>s.kind==='muro')) for(const segment of wallPlanSegments(wall)) {
 const ends=[`${X(segment.a.x)},${Y(segment.a.y)}`,`${X(segment.b.x)},${Y(segment.b.y)}`].sort();
 wallLines.add(ends.join(' '));
}
const walls=[...wallLines].map(coords=>`<polyline points="${coords}"/>`).join('');
const furniture=objects.filter(o=>o.layer==='arredi').map(svgObject).join('');
const lights=objects.filter(o=>o.layer==='luci').map(svgObject).join('');
const openings=objects.filter(o=>o.layer==='aperture').map(svgObject).join('');
let verticals=0;
const paths=tracciati.map(t=>{const p=puntiRisolti(t,objects);return p.slice(1).map((b:any,i:number)=>{const a=p[i];if(eVerticale(a,b)){verticals++;return `<g data-vertical-metres="${n(Math.abs(b.quotaM-a.quotaM))}"><circle cx="${X(a.x)}" cy="${Y(a.y)}" r="4" fill="#fff"/><path d="M ${X(a.x)-2} ${Y(a.y)}h4"/><title>Tratto verticale: ${n(Math.abs(b.quotaM-a.quotaM))} m</title></g>`}return `<path d="M ${X(a.x)} ${Y(a.y)} L ${X(b.x)} ${Y(b.y)}" stroke-dasharray="5 5"/>`}).join('')}).join('');
const svg=`<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 720 560" role="img" aria-labelledby="demo-plan-title demo-plan-desc">
<title id="demo-plan-title">Casa esempio: pianta dimostrativa di 48 metri quadrati</title><desc id="demo-plan-desc">Soggiorno e cucina, camera e bagno. Arredi, tre strisce LED per nove metri, quattro percorsi luce con quote verticali. Tutti i dati sono inventati.</desc>
<g fill="#f7f8f8">${rooms.map(r=>`<rect x="${X(r.layout.x)}" y="${Y(r.layout.y)}" width="${n(r.layout.w*S)}" height="${n(r.layout.h*S)}"/>`).join('')}</g>
<g id="demo-arredi" color="#84919b">${furniture}</g>
<g id="demo-percorsi" stroke="#367577" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round" fill="none">${paths}<g color="#367577" stroke="none">${objects.filter(o=>o.typeId==='quadro').map(svgObject).join('')}</g></g>
<g id="demo-luci" color="#b47b00">${lights}</g>
<g id="demo-walls" fill="none" stroke="#0b2545" stroke-width="4" stroke-linecap="square">${walls}</g>
<g color="#0b2545">${openings}</g>
<g fill="#0b2545" font-family="Inter, Arial, sans-serif" text-anchor="middle"><text x="${X(1.65)}" y="${Y(2.1)}" font-size="12" font-weight="600">Soggiorno e cucina</text><text x="${X(1.65)}" y="${Y(2.1)+16}" font-size="10" fill="#647584">30 m²</text><text x="${X(6.5)}" y="${Y(2.43)}" font-size="11" font-weight="600">Camera · 10,8 m²</text><text x="${X(5.95)}" y="${Y(5.62)}" font-size="12" font-weight="600">Bagno</text><text x="${X(5.95)}" y="${Y(5.62)+15}" font-size="10" fill="#647584">7,2 m²</text></g>
<g id="demo-dimensions" fill="none" stroke="#89969e" stroke-width="0.8"><path d="M96 68V42M624 68V42M96 48H624M83 80H58M83 476H58M65 80V476"/><path d="M92 52l8 -8M620 52l8 -8M61 84l8 -8M61 480l8 -8"/></g><g font-family="Inter, Arial, sans-serif" font-size="11" fill="#516373" text-anchor="middle"><rect x="341" y="39" width="38" height="17" fill="#fff"/><text x="360" y="51">8,00 m</text><text transform="translate(53 278) rotate(-90)">6,00 m</text></g>
</svg>`;
await writeFile(resolve(output,'house-demo.svg'),svg);
await writeFile(resolve(output,'snapshot.json'),JSON.stringify(snapshot,null,2)+'\n');
const pdfs=await Promise.all(files.map(async(f:any)=>({name:f.name,pages:(await PDFDocument.load(f.bytes)).getPageCount(),bytes:f.bytes.length})));
const metadata={project:snapshot.house.house,fictional:true,exportedAt:snapshot.exportedAt,dimensionsM:{width:8,height:6},roomCount:rooms.length,ledStripCount:3,lightObjectCount:objects.filter(o=>o.layer==='luci').length,furnitureCount:objects.filter(o=>o.layer==='arredi').length,openingCount:objects.filter(o=>o.layer==='aperture').length,routeCount:tracciati.length,verticalSegmentCount:verticals,...quantities.totals,displayTotals:{area:'48 m²',led:'9 m',routes:'10,68 m',verticals:'1,30 m'},pdfs,zip:'downloads/house-core-esempio.zip',svg:'assets/house-demo/house-demo.svg',preview:'assets/house-demo/general-preview.png',groups:['demo-arredi','demo-luci','demo-percorsi'],renderer:'House Core buildLayerPdfs + zipLayerPdfs',symbols:'House Core PlanSymbols.tsx planSymbolLayers',syntheticDataset:true,aiGeneratedRaster:false};
await writeFile(resolve(output,'metadata.json'),JSON.stringify(metadata,null,2)+'\n');
await writeFile(resolve(output,'quantities.json'),JSON.stringify(quantities,null,2)+'\n');
const preview=resolve(output,'general-preview');
execFileSync('pdftoppm',['-f','1','-singlefile','-scale-to','1680','-png',resolve(output,files[0].name),preview]);
await writeFile(resolve(output,'general-preview.provenance.json'),JSON.stringify({sourcePdf:files[0].name,sourcePage:1,renderer:'House Core export-config.ts buildLayerPdfs; rasterized by pdftoppm',dataset:'Entirely fictional public demonstration, authored in generate-house-demo.mts',privateHouseDataUsed:false,aiGeneratedRaster:false,createdAt:snapshot.exportedAt},null,2)+'\n');
console.log(JSON.stringify(metadata,null,2));
