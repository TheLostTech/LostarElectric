// Footer.jsx
function Footer({ lang }) {
  const t = {
    it: { tag:'Quadri e armadi elettrici industriali', prod:'Prodotti', serv:'Servizi', az:'Azienda', contact:'Contatti',
      links:[['Quadri BT','Quadri MT','Armadi di comando','Quadri di potenza'],
             ['Progettazione','Cablaggio','Collaudo','Assistenza'],
             ['Chi siamo','Certificazioni','Officina','Lavora con noi']],
      addr:'Via dell\'Industria 00 · 00000 Città (XX)', vat:'P.IVA 00000000000',
      rights:'© 2026 Lostar S.r.l. · Tutti i diritti riservati', cert:'Conformi IEC 61439-1/2 · Certificazione ISO 9001' },
    en: { tag:'Industrial electrical panels & enclosures', prod:'Products', serv:'Services', az:'Company', contact:'Contact',
      links:[['LV panels','MV panels','Control cabinets','Power panels'],
             ['Engineering','Wiring','Testing','Support'],
             ['About','Certifications','Workshop','Careers']],
      addr:'Via dell\'Industria 00 · 00000 City (XX)', vat:'VAT IT00000000000',
      rights:'© 2026 Lostar S.r.l. · All rights reserved', cert:'IEC 61439-1/2 compliant · ISO 9001 certified' },
  }[lang];
  return (
    <footer style={{background:'var(--lostar-blue-900)',color:'#fff',paddingTop:64,paddingBottom:32}}>
      <div className="container">
        <div style={{display:'grid',gridTemplateColumns:'2fr 1fr 1fr 1fr',gap:32}}>
          <div>
            <img src="../../assets/logo-lostar-reverse.svg" alt="Lostar" style={{height:32,marginBottom:12}}/>
            <p style={{color:'rgba(255,255,255,0.72)',fontSize:14,maxWidth:280}}>{t.tag}</p>
            <div style={{marginTop:20,display:'flex',flexDirection:'column',gap:8,fontSize:13,color:'rgba(255,255,255,0.72)'}}>
              <div style={{display:'flex',gap:8,alignItems:'center'}}><IconPin size={14}/>{t.addr}</div>
              <div style={{display:'flex',gap:8,alignItems:'center'}}><IconMail size={14}/>info@lostar.it</div>
              <div style={{display:'flex',gap:8,alignItems:'center'}}><IconPhone size={14}/>+39 000 000 0000</div>
            </div>
          </div>
          {[t.prod,t.serv,t.az].map((title,i)=>(
            <div key={i}>
              <div style={{fontSize:12,fontWeight:600,letterSpacing:'0.08em',textTransform:'uppercase',color:'var(--lostar-yellow-500)',marginBottom:14}}>{title}</div>
              <ul style={{listStyle:'none',display:'flex',flexDirection:'column',gap:10}}>
                {t.links[i].map(l=><li key={l}><a href="#" style={{color:'rgba(255,255,255,0.8)',fontSize:14}}>{l}</a></li>)}
              </ul>
            </div>
          ))}
        </div>
        <div style={{marginTop:48,paddingTop:24,borderTop:'1px solid rgba(255,255,255,0.1)',display:'flex',justifyContent:'space-between',flexWrap:'wrap',gap:12}}>
          <div className="mono" style={{fontSize:11,color:'rgba(255,255,255,0.5)',letterSpacing:'0.04em',textTransform:'uppercase'}}>{t.cert}</div>
          <div className="mono" style={{fontSize:11,color:'rgba(255,255,255,0.5)',letterSpacing:'0.04em'}}>{t.rights} · {t.vat}</div>
        </div>
      </div>
    </footer>
  );
}
window.Footer = Footer;
