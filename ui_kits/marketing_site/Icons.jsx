// Icons.jsx — small Lucide-style SVG icons, 1.5px stroke, 24x24
const Icon = ({ path, size = 20, ...rest }) => (
  <svg xmlns="http://www.w3.org/2000/svg" width={size} height={size}
       viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5"
       strokeLinecap="round" strokeLinejoin="round" {...rest}>
    {path}
  </svg>
);

const IconZap = (p) => <Icon {...p} path={<polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>} />;
const IconShield = (p) => <Icon {...p} path={<path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>} />;
const IconFactory = (p) => <Icon {...p} path={<path d="M17 14V2H7v12M3 22V10l5 4h8l5-4v12"/>} />;
const IconWrench = (p) => <Icon {...p} path={<path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/>} />;
const IconCpu = (p) => <Icon {...p} path={<><rect x="4" y="4" width="16" height="16" rx="2"/><rect x="9" y="9" width="6" height="6"/><path d="M9 2v2M15 2v2M9 20v2M15 20v2M2 9h2M2 15h2M20 9h2M20 15h2"/></>} />;
const IconCheck = (p) => <Icon {...p} path={<polyline points="20 6 9 17 4 12"/>} />;
const IconArrowRight = (p) => <Icon {...p} path={<><path d="M5 12h14"/><path d="M13 5l7 7-7 7"/></>} />;
const IconMenu = (p) => <Icon {...p} path={<><path d="M3 6h18M3 12h18M3 18h18"/></>} />;
const IconMail = (p) => <Icon {...p} path={<><rect x="3" y="5" width="18" height="14" rx="2"/><path d="M3 7l9 7 9-7"/></>} />;
const IconPhone = (p) => <Icon {...p} path={<path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>} />;
const IconPin = (p) => <Icon {...p} path={<><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></>} />;
const IconDownload = (p) => <Icon {...p} path={<><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><path d="M7 10l5 5 5-5"/><path d="M12 15V3"/></>} />;

Object.assign(window, { Icon, IconZap, IconShield, IconFactory, IconWrench, IconCpu, IconCheck, IconArrowRight, IconMenu, IconMail, IconPhone, IconPin, IconDownload });
