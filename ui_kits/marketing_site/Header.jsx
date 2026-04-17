// Header.jsx — sticky top nav
function Header({ page, setPage, lang, setLang }) {
  const [scrolled, setScrolled] = React.useState(false);
  React.useEffect(() => {
    const h = () => setScrolled(window.scrollY > 80);
    window.addEventListener('scroll', h); return () => window.removeEventListener('scroll', h);
  }, []);
  const t = {
    it: { home:'Home', servizi:'Servizi', chi:'Chi siamo', contatti:'Contatti', cta:'Richiedi preventivo' },
    en: { home:'Home', servizi:'Services', chi:'About', contatti:'Contact', cta:'Get a quote' },
  }[lang];
  const nav = [['home',t.home],['servizi',t.servizi],['chi',t.chi],['contatti',t.contatti]];
  return (
    <header style={{
      position:'sticky', top:0, zIndex:50,
      background: scrolled ? 'rgba(255,255,255,0.92)' : '#fff',
      backdropFilter: scrolled ? 'blur(12px)' : 'none',
      borderBottom:'1px solid var(--border-subtle)',
      transition:'all 180ms cubic-bezier(0.2,0,0,1)',
    }}>
      <div style={{height:4,background:'var(--lostar-yellow-500)'}}/>
      <div className="container" style={{height:72,display:'flex',alignItems:'center',justifyContent:'space-between',gap:24}}>
        <button onClick={()=>setPage('home')} style={{display:'flex',alignItems:'center'}}>
          <img src="../../assets/logo-lostar.svg" alt="Lostar" style={{height:28}}/>
        </button>
        <nav className="hide-mobile" style={{display:'flex',gap:28}}>
          {nav.map(([k,label])=>(
            <button key={k} onClick={()=>setPage(k)}
              style={{
                fontSize:14, fontWeight:600,
                color: page===k ? 'var(--lostar-blue-800)' : 'var(--gray-600)',
                borderBottom: page===k ? '2px solid var(--lostar-yellow-500)':'2px solid transparent',
                paddingBottom:4, transition:'all 180ms',
              }}>{label}</button>
          ))}
        </nav>
        <div style={{display:'flex',alignItems:'center',gap:16}}>
          <div className="mono" style={{display:'flex',gap:4,fontSize:12,fontWeight:600}}>
            <button onClick={()=>setLang('it')} style={{color: lang==='it' ? 'var(--lostar-blue-800)' : 'var(--gray-500)'}}>IT</button>
            <span style={{color:'var(--gray-300)'}}>/</span>
            <button onClick={()=>setLang('en')} style={{color: lang==='en' ? 'var(--lostar-blue-800)' : 'var(--gray-500)'}}>EN</button>
          </div>
          <button className="btn btn--primary hide-mobile" onClick={()=>setPage('contatti')}>
            {t.cta} <IconArrowRight size={16}/>
          </button>
        </div>
      </div>
    </header>
  );
}
window.Header = Header;
