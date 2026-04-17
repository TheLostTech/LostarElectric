// Pages.jsx — all four page screens
function HomePage({ lang, setPage }) {
  const t = {
    it: {
      eye:'QUADRI E ARMADI ELETTRICI INDUSTRIALI',
      h1:'Quadri elettrici che non ti lasciano a terra. Da trent\'anni.',
      lead:'Progettiamo, cabliamo e collaudiamo in officina. Ti consegniamo il quadro già testato, con la documentazione pronta e lo schema as-built.',
      cta1:'Richiedi preventivo', cta2:'Scarica il catalogo',
      whatEye:'COSA FACCIAMO', whatTitle:'Dal disegno al collaudo. In officina. Con le tue norme addosso.',
      stats:[['30','anni di officina'],['1.200','quadri l\'anno'],['48h','tempo di risposta'],['100%','collaudati prima della spedizione']],
      servEye:'SERVIZI', servTitle:'Quattro cose, fatte bene.',
      servs:[
        {icon:'cpu',   title:'Progettazione',  desc:'Schemi elettrici e layout carpenteria. Dal funzionale al costruttivo.', spec:'IEC 61439 · EPLAN · AutoCAD'},
        {icon:'zap',   title:'Cablaggio',      desc:'Cablaggio in officina con cura artigianale. Percorsi puliti, etichette a vita.', spec:'UL/CSA su richiesta'},
        {icon:'shield',title:'Collaudo',       desc:'Prove di tipo e prove individuali. Tensione applicata, isolamento, funzionale.', spec:'100% sui quadri spediti'},
        {icon:'wrench',title:'Assistenza',     desc:'Modifiche, ampliamenti, retrofit. Anche su quadri che non abbiamo fatto noi.', spec:'Ticket → risposta 48h'},
      ],
      finalEye:'PARLIAMONE', finalH:'Mandaci lo schema. Ti rispondiamo in 48 ore.',
      finalP:'Un preventivo vero, non una forchetta. Se hai un\'idea, ci lavoriamo sopra — anche se è ancora uno scarabocchio.',
    },
    en: {
      eye:'INDUSTRIAL ELECTRICAL PANELS & ENCLOSURES',
      h1:'Electrical panels that don\'t leave you stranded. For thirty years.',
      lead:'We design, wire and test in the workshop. You get the panel already tested, with the documentation ready and the as-built schematic.',
      cta1:'Get a quote', cta2:'Download catalogue',
      whatEye:'WHAT WE DO', whatTitle:'From drawing to testing. In-house. With your standards on our back.',
      stats:[['30','years in the workshop'],['1,200','panels per year'],['48h','response time'],['100%','tested before shipping']],
      servEye:'SERVICES', servTitle:'Four things, done well.',
      servs:[
        {icon:'cpu',   title:'Engineering',   desc:'Schematics and cabinet layouts. From functional to manufacturing.', spec:'IEC 61439 · EPLAN · AutoCAD'},
        {icon:'zap',   title:'Wiring',        desc:'Workshop wiring with craftsmanship. Clean runs, labels for life.', spec:'UL/CSA on request'},
        {icon:'shield',title:'Testing',       desc:'Type and routine tests. Applied voltage, insulation, functional.', spec:'100% of shipped panels'},
        {icon:'wrench',title:'Support',       desc:'Modifications, extensions, retrofits. Even on panels we didn\'t build.', spec:'Ticket → 48h response'},
      ],
      finalEye:'LET\'S TALK', finalH:'Send us the drawing. We reply in 48 hours.',
      finalP:'A real quote, not a range. If you have an idea, we\'ll work on it — even if it\'s still a sketch.',
    },
  }[lang];
  const iconMap = { cpu:IconCpu, zap:IconZap, shield:IconShield, wrench:IconWrench };
  return (
    <>
      {/* HERO — split: white center, accent right */}
      <section style={{position:'relative',background:'#fff',overflow:'hidden',borderBottom:'1px solid var(--border-subtle)'}}>
        <div className="container" style={{display:'grid',gridTemplateColumns:'1fr minmax(280px,420px)',gap:48,alignItems:'stretch',paddingTop:80,paddingBottom:80,minHeight:520}}>
          {/* Center: text on white */}
          <div style={{display:'flex',flexDirection:'column',justifyContent:'center'}}>
            <div className="eyebrow">{t.eye}</div>
            <h1 style={{fontSize:'clamp(40px,5.2vw,68px)',fontWeight:800,letterSpacing:'-0.02em',lineHeight:1.05,marginTop:16,color:'var(--lostar-blue-900)'}}>{t.h1}</h1>
            <p style={{fontSize:20,color:'var(--fg-secondary)',lineHeight:1.5,marginTop:20,maxWidth:560}}>{t.lead}</p>
            <div className="row" style={{marginTop:32}}>
              <button className="btn btn--primary" onClick={()=>setPage('contatti')}>{t.cta1} <IconArrowRight size={16}/></button>
              <button className="btn btn--secondary"><IconDownload size={16}/>{t.cta2}</button>
            </div>
          </div>
          {/* Right: accent panel with photo */}
          <div style={{position:'relative',borderRadius:4,overflow:'hidden',background:'var(--lostar-yellow-500)',minHeight:360}}>
            <div style={{position:'absolute',inset:0,backgroundImage:'url(../../assets/placeholder-quadro.svg)',backgroundSize:'cover',backgroundPosition:'center',mixBlendMode:'multiply',opacity:0.9}}/>
            <div style={{position:'absolute',inset:0,background:'linear-gradient(180deg, rgba(244,181,0,0) 40%, rgba(244,181,0,0.85) 100%)'}}/>
            <div style={{position:'absolute',left:20,right:20,bottom:20}}>
              <div className="mono" style={{fontSize:11,fontWeight:600,letterSpacing:'0.08em',textTransform:'uppercase',color:'var(--lostar-blue-900)'}}>QUADRO BT · QDB SERIES</div>
              <div style={{fontFamily:'var(--font-display)',fontSize:22,fontWeight:800,color:'var(--lostar-blue-900)',marginTop:4,letterSpacing:'-0.01em'}}>IP65 · 4000 A · Forma 4b</div>
            </div>
          </div>
        </div>
      </section>

      {/* STATS */}
      <section className="section--brand section--tight">
        <div className="container grid-4">
          {t.stats.map(([n,l])=>(<div key={l} className="stat"><div className="num">{n}</div><div className="lbl">{l}</div></div>))}
        </div>
      </section>

      {/* WHAT */}
      <section className="section">
        <div className="container">
          <div className="eyebrow">{t.whatEye}</div>
          <h2 style={{fontSize:'clamp(32px,3.4vw,44px)',fontWeight:700,letterSpacing:'-0.02em',lineHeight:1.15,marginTop:12,maxWidth:800}}>{t.whatTitle}</h2>
          <div className="grid-2" style={{marginTop:48}}>
            <div style={{borderRadius:4,overflow:'hidden',background:`url(../../assets/placeholder-officina.svg) center/cover`,aspectRatio:'4/3'}}/>
            <div style={{display:'flex',flexDirection:'column',justifyContent:'center',gap:16}}>
              <div className="row" style={{gap:8}}>
                <span className="badge">IEC 61439-1/2</span><span className="badge">ISO 9001</span><span className="badge">CE</span>
              </div>
              <p style={{fontSize:18,lineHeight:1.6,color:'var(--fg-secondary)'}}>Officina di 1.800 m². Reparto progettazione, reparto cablaggio, reparto collaudo. Magazzino componenti a scorta per tagliare i tempi. Gli ingegneri parlano con chi cabla. Chi cabla parla con chi collauda. Il risultato: quadri consegnati quando serve, non prima, non dopo.</p>
              <div className="row" style={{gap:6,marginTop:8}}><IconCheck size={18} style={{color:'var(--status-live)'}}/><span style={{fontWeight:500}}>Lead time dichiarato, rispettato.</span></div>
              <div className="row" style={{gap:6}}><IconCheck size={18} style={{color:'var(--status-live)'}}/><span style={{fontWeight:500}}>Un interlocutore dal preventivo alla consegna.</span></div>
              <div className="row" style={{gap:6}}><IconCheck size={18} style={{color:'var(--status-live)'}}/><span style={{fontWeight:500}}>Documentazione completa, in formato che puoi davvero usare.</span></div>
            </div>
          </div>
        </div>
      </section>

      {/* SERVICES */}
      <section className="section section--subtle">
        <div className="container">
          <div className="eyebrow">{t.servEye}</div>
          <h2 style={{fontSize:'clamp(32px,3.4vw,44px)',fontWeight:700,letterSpacing:'-0.02em',marginTop:12,marginBottom:40}}>{t.servTitle}</h2>
          <div className="grid-4">
            {t.servs.map(s => {
              const I = iconMap[s.icon];
              return (
                <div key={s.title} className="card-surface" style={{display:'flex',flexDirection:'column',gap:12}}>
                  <div style={{width:44,height:44,background:'var(--lostar-blue-050)',borderRadius:4,display:'flex',alignItems:'center',justifyContent:'center',color:'var(--lostar-blue-800)'}}><I size={22}/></div>
                  <div style={{fontSize:18,fontWeight:700,color:'var(--fg-primary)'}}>{s.title}</div>
                  <p style={{fontSize:14,color:'var(--fg-secondary)',lineHeight:1.5,flex:1}}>{s.desc}</p>
                  <div className="mono" style={{fontSize:11,fontWeight:600,letterSpacing:'0.04em',textTransform:'uppercase',color:'var(--fg-muted)',paddingTop:12,borderTop:'1px solid var(--border-subtle)'}}>{s.spec}</div>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="section" style={{background:'var(--lostar-blue-800)',color:'#fff',borderTop:'4px solid var(--lostar-yellow-500)'}}>
        <div className="container" style={{display:'flex',justifyContent:'space-between',alignItems:'center',gap:32,flexWrap:'wrap'}}>
          <div style={{maxWidth:640}}>
            <div className="eyebrow" style={{color:'var(--lostar-yellow-500)'}}>{t.finalEye}</div>
            <h2 style={{fontSize:'clamp(28px,3vw,40px)',fontWeight:700,letterSpacing:'-0.02em',marginTop:10,marginBottom:10}}>{t.finalH}</h2>
            <p style={{color:'rgba(255,255,255,0.78)',fontSize:17,lineHeight:1.5}}>{t.finalP}</p>
          </div>
          <button className="btn btn--accent" onClick={()=>setPage('contatti')}>{t.cta1} <IconArrowRight size={16}/></button>
        </div>
      </section>
    </>
  );
}

function ServicesPage({ lang, setPage }) {
  const t = {
    it: { eye:'SERVIZI', h1:'Quattro reparti, un quadro.', lead:'Ogni quadro che esce da qui è passato per tutte e quattro le mani. È così che si fa un lavoro fatto bene.',
      items:[
        {n:'01',title:'Progettazione',desc:'Partiamo dal tuo funzionale e tiriamo fuori lo schema elettrico, il layout di carpenteria, la distinta componenti. Se preferisci consegnarci un EPLAN già fatto, lo prendiamo come base. Se invece hai solo una lista di motori da comandare, ti costruiamo tutto.',spec:'IEC 61439 · EPLAN P8 · AutoCAD Electrical'},
        {n:'02',title:'Cablaggio',desc:'Cablaggio in officina da tecnici che lavorano qui da anni. Cavi siglati a entrambe le estremità, canaline dimensionate, percorsi puliti. Serraggi a coppia dichiarata.',spec:'Cavi H07V-K · canalina Legrand · siglatura Cembre'},
        {n:'03',title:'Collaudo',desc:'Prove di tipo sui componenti critici. Prove individuali al 100% dei quadri: continuità del circuito di protezione, resistenza di isolamento, tensione applicata, prova funzionale.',spec:'Rapporto di prova firmato a corredo'},
        {n:'04',title:'Assistenza',desc:'Modifiche e retrofit in officina o sul campo. Anche su quadri che non abbiamo fatto noi — basta che ci mandi lo schema o che venga un nostro tecnico a rilevarlo.',spec:'Interventi su Nord Italia entro 48h'},
      ] },
    en: { eye:'SERVICES', h1:'Four departments, one panel.', lead:'Every panel that leaves here has passed through all four pairs of hands. That\'s how you build something right.',
      items:[
        {n:'01',title:'Engineering',desc:'We start from your functional spec and produce the electrical schematic, cabinet layout and BOM. If you prefer to hand us an EPLAN project, we use it as the base. If all you have is a motor list, we build the whole thing.',spec:'IEC 61439 · EPLAN P8 · AutoCAD Electrical'},
        {n:'02',title:'Wiring',desc:'Workshop wiring by technicians who have been here for years. Cables tagged at both ends, cable ducts properly sized, clean runs. Terminations torqued to spec.',spec:'H07V-K cable · Legrand ducting · Cembre labels'},
        {n:'03',title:'Testing',desc:'Type tests on critical components. Routine tests on 100% of panels: protective-circuit continuity, insulation resistance, applied voltage, functional test.',spec:'Signed test report shipped with the panel'},
        {n:'04',title:'Support',desc:'Modifications and retrofits in the workshop or on site. Even on panels we didn\'t build — just send us the schematic or we\'ll come survey it.',spec:'Northern Italy on-site within 48h'},
      ] },
  }[lang];
  return (
    <>
      <section className="section" style={{paddingTop:80,paddingBottom:40}}>
        <div className="container" style={{maxWidth:880}}>
          <div className="eyebrow">{t.eye}</div>
          <h1 style={{fontSize:'clamp(36px,4.5vw,60px)',fontWeight:800,letterSpacing:'-0.02em',lineHeight:1.05,marginTop:14,marginBottom:18}}>{t.h1}</h1>
          <p style={{fontSize:20,color:'var(--fg-secondary)',lineHeight:1.5}}>{t.lead}</p>
        </div>
      </section>
      <section style={{paddingBottom:96}}>
        <div className="container">
          {t.items.map((s,i)=>(
            <div key={s.n} style={{display:'grid',gridTemplateColumns:'80px 1fr 2fr',gap:32,padding:'32px 0',borderTop:'1px solid var(--border-subtle)'}}>
              <div className="mono" style={{fontSize:14,fontWeight:600,color:'var(--lostar-blue-800)',letterSpacing:'0.04em'}}>{s.n}</div>
              <h3 style={{fontSize:24,fontWeight:700,letterSpacing:'-0.01em'}}>{s.title}</h3>
              <div>
                <p style={{fontSize:16,lineHeight:1.6,color:'var(--fg-primary)'}}>{s.desc}</p>
                <div className="mono" style={{marginTop:14,fontSize:11,fontWeight:600,letterSpacing:'0.04em',textTransform:'uppercase',color:'var(--fg-muted)'}}>{s.spec}</div>
              </div>
            </div>
          ))}
        </div>
      </section>
    </>
  );
}

function AboutPage({ lang }) {
  const t = {
    it: { eye:'CHI SIAMO', h1:'Trent\'anni di quadri, fatti bene.',
      p1:'Lostar nasce nel 1995 come piccola officina di cablaggio per l\'industria locale. Tre persone, un banco, un saldatore. La storia di tante realtà italiane.',
      p2:'Oggi siamo un\'officina da 1.800 m², con un reparto di progettazione, uno di cablaggio e uno di collaudo. Produciamo circa 1.200 quadri l\'anno per l\'industria manifatturiera, per impiantisti, per studi di progettazione.',
      p3:'Quello che non è cambiato è l\'approccio: un cliente è una persona, un quadro è un oggetto serio, e quando qualcosa non funziona si risponde al telefono.',
      vEye:'VALORI', vTitle:'Tre cose a cui teniamo.',
      values:[
        {t:'Precisione',d:'Un quadro è un oggetto ingegneristico. O è giusto, o non lo è. Non c\'è una via di mezzo.'},
        {t:'Parola',   d:'Se diciamo che consegniamo il 15 del mese, consegniamo il 15. Se non ce la facciamo, te lo diciamo con anticipo.'},
        {t:'Memoria',  d:'Il quadro che ti abbiamo fatto dieci anni fa lo sappiamo ancora leggere. Se hai un problema, partiamo dallo schema vero.'},
      ],
      certEye:'CERTIFICAZIONI & NORME', certNote:'I nostri quadri sono conformi a IEC 61439-1 e IEC 61439-2. L\'azienda è certificata ISO 9001. Marcatura CE su tutti i quadri destinati al mercato EU.' },
    en: { eye:'ABOUT', h1:'Thirty years of panels, done right.',
      p1:'Lostar started in 1995 as a small wiring workshop serving local industry. Three people, one bench, one soldering iron. The usual Italian small-business story.',
      p2:'Today we run an 1,800 m² workshop with departments for engineering, wiring and testing. We produce around 1,200 panels per year for manufacturers, installers and design firms.',
      p3:'What hasn\'t changed is the approach: a client is a person, a panel is a serious object, and when something goes wrong somebody picks up the phone.',
      vEye:'VALUES', vTitle:'Three things we care about.',
      values:[
        {t:'Precision',d:'A panel is an engineered object. Either it\'s right, or it isn\'t. There\'s no in-between.'},
        {t:'Word',     d:'If we say we deliver on the 15th, we deliver on the 15th. If we can\'t, we tell you in advance.'},
        {t:'Memory',   d:'The panel we built ten years ago — we can still read it. If you have a problem, we start from the real schematic.'},
      ],
      certEye:'CERTIFICATIONS & STANDARDS', certNote:'Our panels comply with IEC 61439-1 and IEC 61439-2. The company is ISO 9001 certified. CE marking on all panels for the EU market.' },
  }[lang];
  return (
    <>
      <section className="section">
        <div className="container" style={{display:'grid',gridTemplateColumns:'1fr 1fr',gap:48,alignItems:'center'}}>
          <div>
            <div className="eyebrow">{t.eye}</div>
            <h1 style={{fontSize:'clamp(36px,4.5vw,60px)',fontWeight:800,letterSpacing:'-0.02em',lineHeight:1.05,marginTop:14,marginBottom:24}}>{t.h1}</h1>
            <p style={{fontSize:17,lineHeight:1.6,color:'var(--fg-secondary)',marginBottom:14}}>{t.p1}</p>
            <p style={{fontSize:17,lineHeight:1.6,color:'var(--fg-secondary)',marginBottom:14}}>{t.p2}</p>
            <p style={{fontSize:17,lineHeight:1.6,color:'var(--fg-primary)',fontWeight:500}}>{t.p3}</p>
          </div>
          <div style={{aspectRatio:'4/5',borderRadius:4,backgroundImage:'url(../../assets/placeholder-officina.svg)',backgroundSize:'cover',backgroundPosition:'center'}}/>
        </div>
      </section>
      <section className="section section--subtle">
        <div className="container">
          <div className="eyebrow">{t.vEye}</div>
          <h2 style={{fontSize:'clamp(30px,3vw,40px)',fontWeight:700,letterSpacing:'-0.02em',marginTop:10,marginBottom:32}}>{t.vTitle}</h2>
          <div className="grid-3">
            {t.values.map((v,i)=>(
              <div key={v.t} style={{borderTop:'3px solid var(--lostar-yellow-500)',paddingTop:20}}>
                <div className="mono" style={{fontSize:12,fontWeight:600,color:'var(--fg-muted)',letterSpacing:'0.04em'}}>0{i+1}</div>
                <h3 style={{fontSize:22,fontWeight:700,marginTop:8,marginBottom:10}}>{v.t}</h3>
                <p style={{fontSize:15,lineHeight:1.6,color:'var(--fg-secondary)'}}>{v.d}</p>
              </div>
            ))}
          </div>
        </div>
      </section>
      <section className="section--brand section">
        <div className="container">
          <div className="eyebrow" style={{color:'var(--lostar-yellow-500)'}}>{t.certEye}</div>
          <div style={{display:'flex',gap:14,flexWrap:'wrap',marginTop:16,marginBottom:24}}>
            {['IEC 61439-1','IEC 61439-2','ISO 9001','CE','RoHS','EN 60204-1'].map(c=>(
              <div key={c} className="mono" style={{border:'1px solid rgba(255,255,255,0.2)',padding:'10px 16px',fontSize:13,fontWeight:600,letterSpacing:'0.04em',color:'#fff'}}>{c}</div>
            ))}
          </div>
          <p style={{color:'rgba(255,255,255,0.75)',fontSize:16,maxWidth:720,lineHeight:1.5}}>{t.certNote}</p>
        </div>
      </section>
    </>
  );
}

function ContactPage({ lang }) {
  const t = {
    it: { eye:'CONTATTI', h1:'Mandaci lo schema. Ti rispondiamo in 48 ore.',
      lead:'Compila il form, o scrivici direttamente. Un preventivo vero, non una forchetta.',
      f:{rag:'Ragione sociale',name:'Nome e cognome',email:'Email',tel:'Telefono',type:'Tipologia quadro',msg:'Descrivi il progetto',file:'Allega schema (opzionale)'},
      types:['Quadro di distribuzione BT','Quadro di potenza / MCC','Armadio di comando','Quadro MT','Altro / non so ancora'],
      submit:'Invia richiesta', success:'Richiesta ricevuta. Ti ricontattiamo entro 48 ore.',
      direct:'Oppure scrivici direttamente', resp:'Risposta entro 48h nei giorni feriali.' },
    en: { eye:'CONTACT', h1:'Send us the drawing. We reply in 48 hours.',
      lead:'Fill the form, or write to us directly. A real quote, not a range.',
      f:{rag:'Company',name:'Full name',email:'Email',tel:'Phone',type:'Panel type',msg:'Describe the project',file:'Attach schematic (optional)'},
      types:['LV distribution panel','Power panel / MCC','Control cabinet','MV panel','Other / not sure yet'],
      submit:'Send request', success:'Request received. We\'ll be in touch within 48 hours.',
      direct:'Or write to us directly', resp:'Response within 48h on business days.' },
  }[lang];
  const [sent, setSent] = React.useState(false);
  return (
    <section className="section" style={{paddingBottom:120}}>
      <div className="container" style={{display:'grid',gridTemplateColumns:'3fr 2fr',gap:64}}>
        <div>
          <div className="eyebrow">{t.eye}</div>
          <h1 style={{fontSize:'clamp(32px,4vw,52px)',fontWeight:800,letterSpacing:'-0.02em',lineHeight:1.1,marginTop:14,marginBottom:14}}>{t.h1}</h1>
          <p style={{fontSize:18,color:'var(--fg-secondary)',lineHeight:1.5,marginBottom:40}}>{t.lead}</p>
          {sent ? (
            <div style={{background:'var(--status-live-bg)',border:'1px solid #B8DDCA',borderRadius:4,padding:24,display:'flex',gap:14,alignItems:'center'}}>
              <div style={{width:40,height:40,borderRadius:999,background:'var(--status-live)',color:'#fff',display:'flex',alignItems:'center',justifyContent:'center',flex:'none'}}><IconCheck size={22}/></div>
              <p style={{fontWeight:500,color:'var(--gray-800)'}}>{t.success}</p>
            </div>
          ) : (
            <form onSubmit={e=>{e.preventDefault();setSent(true);window.scrollTo({top:0,behavior:'smooth'});}} className="stack" style={{gap:18}}>
              <div className="grid-2">
                <div className="field"><label>{t.f.rag}</label><input className="inp" required placeholder="Rossi Impianti S.r.l."/></div>
                <div className="field"><label>{t.f.name}</label><input className="inp" required placeholder="Mario Rossi"/></div>
              </div>
              <div className="grid-2">
                <div className="field"><label>{t.f.email}</label><input className="inp" type="email" required placeholder="mario@rossi-impianti.it"/></div>
                <div className="field"><label>{t.f.tel}</label><input className="inp" type="tel" placeholder="+39 000 000 0000"/></div>
              </div>
              <div className="field"><label>{t.f.type}</label>
                <select className="inp">{t.types.map(x=><option key={x}>{x}</option>)}</select>
              </div>
              <div className="field"><label>{t.f.msg}</label>
                <textarea className="inp" required placeholder="Potenza, numero di partenze, norme di riferimento, tempi..."/></div>
              <div className="field"><label>{t.f.file}</label>
                <div style={{border:'1px dashed var(--gray-300)',borderRadius:2,padding:'20px 14px',textAlign:'center',color:'var(--fg-muted)',fontSize:14}}>
                  <IconDownload size={18} style={{display:'inline-block',verticalAlign:'-4px',marginRight:6,transform:'rotate(180deg)'}}/>
                  PDF, DWG, EPLAN · max 20 MB
                </div>
              </div>
              <div className="row" style={{marginTop:8}}>
                <button className="btn btn--primary" type="submit">{t.submit} <IconArrowRight size={16}/></button>
              </div>
            </form>
          )}
        </div>
        <aside>
          <div style={{background:'var(--bg-subtle)',border:'1px solid var(--border-subtle)',borderRadius:4,padding:28}}>
            <div className="eyebrow">{t.direct}</div>
            <div style={{marginTop:18,display:'flex',flexDirection:'column',gap:18}}>
              <div><div className="mono" style={{fontSize:11,color:'var(--fg-muted)',letterSpacing:'0.04em',textTransform:'uppercase'}}>Email</div>
                <a href="mailto:info@lostar.it" style={{fontSize:17,fontWeight:600}}>info@lostar.it</a></div>
              <div><div className="mono" style={{fontSize:11,color:'var(--fg-muted)',letterSpacing:'0.04em',textTransform:'uppercase'}}>Telefono</div>
                <div style={{fontSize:17,fontWeight:600}}>+39 000 000 0000</div>
                <div style={{fontSize:13,color:'var(--fg-muted)',marginTop:2}}>Lun-Ven 8:30 – 17:30</div></div>
              <div><div className="mono" style={{fontSize:11,color:'var(--fg-muted)',letterSpacing:'0.04em',textTransform:'uppercase'}}>Indirizzo</div>
                <div style={{fontSize:15,fontWeight:500,lineHeight:1.5}}>Via dell'Industria 00<br/>00000 Città (XX)<br/>Italia</div></div>
            </div>
            <div style={{marginTop:24,paddingTop:20,borderTop:'1px solid var(--border-subtle)',display:'flex',gap:10,alignItems:'center'}}>
              <div style={{width:8,height:8,borderRadius:999,background:'var(--status-live)'}}/>
              <div className="mono" style={{fontSize:12,color:'var(--gray-700)'}}>{t.resp}</div>
            </div>
          </div>
        </aside>
      </div>
    </section>
  );
}

Object.assign(window, { HomePage, ServicesPage, AboutPage, ContactPage });
