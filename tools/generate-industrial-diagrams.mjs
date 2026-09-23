/** Exact orthographic diagrams for the public Lostar Electric site.
 * Illustrative geometry only: no product, installed equipment, ratings or certification claimed.
 * Run: node tools/generate-industrial-diagrams.mjs
 */
import { mkdir, writeFile } from 'node:fs/promises';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
const out=resolve(dirname(fileURLToPath(import.meta.url)),'../site/assets/industrial');
await mkdir(out,{recursive:true});
const C={navy:'#0b2545',slate:'#627887',light:'#f4f6f6',yellow:'#f4b500',white:'#ffffff'};
const rect=(x,y,w,h,fill='none',stroke=C.navy,sw=2,rx=0)=>`<rect x="${x}" y="${y}" width="${w}" height="${h}" rx="${rx}" fill="${fill}" stroke="${stroke}" stroke-width="${sw}"/>`;
const line=(x1,y1,x2,y2,stroke=C.navy,sw=2)=>`<line x1="${x1}" y1="${y1}" x2="${x2}" y2="${y2}" stroke="${stroke}" stroke-width="${sw}"/>`;
const path=(d,stroke=C.navy,sw=2,fill='none')=>`<path d="${d}" stroke="${stroke}" stroke-width="${sw}" fill="${fill}"/>`;
const circle=(x,y,r,fill='none',stroke=C.navy,sw=2)=>`<circle cx="${x}" cy="${y}" r="${r}" fill="${fill}" stroke="${stroke}" stroke-width="${sw}"/>`;
const text=(x,y,s,size=18,anchor='start',fill=C.navy,weight=500)=>`<text x="${x}" y="${y}" font-size="${size}" text-anchor="${anchor}" fill="${fill}" font-weight="${weight}">${s}</text>`;
const screw=(x,y)=>circle(x,y,4,C.white,C.slate,1.3)+line(x-2,y+2,x+2,y-2,C.slate,1.3);
function rail(x,y,w){return rect(x,y,w,12,C.light,C.slate,1.3)+line(x+8,y+6,x+w-8,y+6,C.slate,1)+screw(x+11,y+6)+screw(x+w-11,y+6);}
function duct(x,y,w,h){let s=rect(x,y,w,h,C.light,C.slate,1.3);for(let at=x+12;at<x+w-8;at+=16)s+=line(at,y,at,y+6,C.slate,1)+line(at,y+h-6,at,y+h,C.slate,1);return s;}
function terminalRow(x,y,count,pitch=28){let s='';for(let i=0;i<count;i++){const xx=x+i*pitch;s+=rect(xx,y,pitch-2,37,i===count-1?C.yellow:C.light,C.navy,1.5)+circle(xx+(pitch-2)/2,y+10,3,C.white,C.navy,1.1)+circle(xx+(pitch-2)/2,y+27,3,C.white,C.navy,1.1)+line(xx+5,y+18,xx+pitch-7,y+18,C.slate,1);}return s;}
function cabinet(x,y,w,h){return rect(x,y,w,h,C.white,C.navy,2.6,2)+rect(x+14,y+14,w-28,h-28,'none',C.slate,1)+[screw(x+8,y+8),screw(x+w-8,y+8),screw(x+8,y+h-8),screw(x+w-8,y+h-8)].join('');}
const port=(x,y,active=false)=>rect(x,y,8,8,active?C.yellow:C.white,C.navy,1.2);
const arrow=(x,y)=>path(`M${x-7} ${y-5}L${x} ${y}L${x-7} ${y+5}`,C.navy,1.6);
const svg=(id,title,desc,body)=>`<svg xmlns="http://www.w3.org/2000/svg" id="${id}" viewBox="0 0 640 560" role="img" aria-labelledby="${id}-title ${id}-desc"><title id="${id}-title">${title}</title><desc id="${id}-desc">${desc}</desc><g font-family="Inter, Arial, sans-serif" stroke-linejoin="round" stroke-linecap="round">${body}</g></svg>\n`;
// PANEL — orthographic enclosure, three equipment families, two DIN rails and explicit conductors.
let p=cabinet(44,32,552,496)+text(78,79,'Quadro di comando',23,'start',C.navy,600);
p+=rail(75,163,490);
// Main disconnect: rotary handle, mounting screws and input/output contacts.
p+=rect(88,117,91,110,C.light,C.navy,1.8)+screw(97,126)+screw(170,218)+circle(133.5,173,26,C.white,C.navy,1.5)+circle(133.5,173,17,C.yellow,C.navy,1.5)+rect(126.5,151,14,44,C.navy,C.navy,1,2);
for(const x of [111,130,149])p+=port(x,108)+port(x,228,true);
p+=text(133,104,'Sezionamento',18,'middle');
// Modular protection with three individual actuators, no invented ratings.
for(let i=0;i<3;i++){let x=233+i*38;p+=rect(x,117,36,110,C.white,C.navy,1.8)+rect(x+8,156,20,35,C.light,C.navy,1.3)+rect(x+10,158,16,11,C.navy,C.navy,1)+port(x+14,107)+port(x+14,228,true);}
p+=text(289,104,'Protezione',18,'middle');
// Controller with exact I/O connection points.
p+=rect(404,117,143,110,C.light,C.navy,1.8)+rect(415,129,120,40,C.white,C.navy,1.3)+text(475,156,'Controllo',18,'middle',C.navy,600);
for(let i=0;i<6;i++)p+=port(417+i*20,182,i===0)+port(417+i*20,210);
p+=text(475,104,'Ingressi / uscite',18,'middle');
// Neatly routed conductors. Vertical segments pass through a slotted cable duct.
const starts=[115,134,153,251,289,327,421,441,461,481,501,521];
for(let i=0;i<starts.length;i++){let x=starts[i],end=111+i*38;p+=path(`M${x} 237V${270+(i%3)*5}H${end}V368`,C.slate,1.45);}
p+=duct(75,299,490,27)+rail(75,390,490);
p+=path('M455 466H566V394H554',C.slate,1.2);
p+=terminalRow(98,375,12,38);
// Ordered outgoing cable bundle and connection markers.
for(let i=0;i<12;i++){let x=116+i*38;p+=path(`M${x} 412V${432+(i%3)*6}H${200+i*12}V479`,C.slate,1.3)+circle(x,416,2.5,C.yellow,'none',0);}
p+=rect(181,471,167,25,C.light,C.navy,1.5)+line(200,480,330,480,C.slate,1.1)+text(377,489,'Morsetti',20,'start',C.navy,600);
await writeFile(resolve(out,'panel.svg'),svg('industrial-panel','Schema illustrativo di un quadro di comando','Vista frontale ortogonale: sezionamento, protezione, controllo, guide DIN, morsetti e percorsi ordinati dei collegamenti. Nessun modello o dato di targa rappresentato.',p));
// AUTOMATION — a readable signal path with distinct sensing and actuation geometries.
let a=text(98,69,'Sensori',22,'middle',C.navy,600)+text(320,69,'Quadro di comando',22,'middle',C.navy,600)+text(541,69,'Attuatori',22,'middle',C.navy,600);
a+=cabinet(231,105,178,348)+rail(246,150,148)+rail(246,313,148);
a+=rect(263,125,114,105,C.light,C.navy,1.6)+rect(274,139,92,34,C.white,C.navy,1.2)+text(320,162,'Controllo',18,'middle',C.navy,600);
for(let i=0;i<4;i++)a+=rect(274+i*24,192,18,22,i===0?C.yellow:C.white,C.navy,1.1);
a+=duct(248,253,144,20)+rect(263,301,114,100,C.white,C.navy,1.6)+text(320,332,'I/O',22,'middle',C.navy,600);
for(let i=0;i<5;i++)a+=port(273+i*20,349)+port(273+i*20,383,i===4);
for(const [i,y] of [167,283,399].entries()){
 a+=path(`M160 ${y}H220`,C.navy,1.7)+arrow(220,y)+circle(160,y,3,C.yellow,'none',0);
 a+=path(`M409 ${y}H475`,C.navy,1.7)+arrow(475,y)+circle(409,y,3,C.yellow,'none',0);
 a+=path(`M232 ${y}H${252+i*4}V${357+i*12}H263`,C.slate,1.15);
}
// Presence sensor: lens and three detection rays.
a+=rect(55,141,71,49,C.light,C.navy,1.6,2)+circle(90.5,165.5,13,C.white,C.navy,1.5)+line(131,151,143,143,C.slate,1.5)+line(134,166,150,166,C.yellow,2)+line(131,180,143,188,C.slate,1.5)+text(98,219,'Presenza',18,'middle');
// Position sensor: roller lever and mechanical contact.
a+=rect(55,268,58,30,C.light,C.navy,1.6)+path('M72 268L102 247L124 258',C.navy,2)+circle(129,260,6,C.white,C.navy,1.5)+line(68,282,95,282,C.yellow,2.3)+text(98,334,'Posizione',18,'middle');
// Process sensor: generic probe connected to a sensing head, with no unit or rating.
a+=circle(88,390,21,C.white,C.navy,1.7)+line(88,411,88,433,C.navy,5)+line(81,433,95,433,C.navy,1.5)+path('M76 393L85 384L100 396',C.slate,1.4)+line(109,390,142,390,C.navy,1.6)+text(98,477,'Processo',18,'middle');
// Motor, valve and signal lamp — orthographic standard geometric symbols.
a+=circle(536,167,30,C.light,C.navy,1.8)+text(536,175,'M',23,'middle',C.navy,600)+line(566,167,594,167,C.navy,2.5)+line(516,192,512,203,C.navy,1.7)+line(556,192,560,203,C.navy,1.7)+line(505,204,567,204,C.navy,1.7)+text(541,233,'Motore',18,'middle');
a+=path('M501 260L537 283L501 306ZM573 260L537 283L573 306Z',C.navy,1.8,C.light)+line(537,283,537,243,C.navy,1.8)+line(520,243,554,243,C.navy,1.8)+circle(537,283,4,C.yellow,C.navy,1)+text(541,341,'Valvola',18,'middle');
a+=circle(537,399,27,C.white,C.navy,1.8)+circle(537,399,14,C.yellow,C.navy,1.5)+line(518,380,556,418,C.navy,1.3)+line(556,380,518,418,C.navy,1.3)+text(541,458,'Segnalazione',18,'middle');
a+=text(320,508,'Rilevazione → Logica → Comando',19,'middle',C.slate);
await writeFile(resolve(out,'automation.svg'),svg('industrial-automation','Schema illustrativo del flusso di automazione','Sensori di presenza, posizione e processo inviano segnali al quadro di comando. Il controllo attraverso ingressi e uscite comanda motore, valvola e segnalazione.',a));
// INSPECTION — specific reference points attached to visible cabinet features.
let v=cabinet(43,50,349,453)+text(73,93,'Punti di controllo',22,'start',C.navy,600);
v+=rail(63,155,306)+rail(63,297,306)+rail(63,420,306);
for(let i=0;i<4;i++){let x=81+i*42;v+=rect(x,124,39,91,C.white,C.navy,1.6)+rect(x+9,151,21,32,C.light,C.navy,1.2)+rect(x+11,153,17,10,C.navy,C.navy,1)+port(x+15,209);}
v+=rect(271,124,69,91,C.light,C.navy,1.7)+rect(282,138,47,30,C.white,C.navy,1.2)+circle(290,187,3,C.yellow,'none',0)+circle(307,187,3,C.slate,'none',0);
for(let i=0;i<8;i++){let x=97+i*30;v+=path(`M${x} 217V${229+(i%3)*6}H${107+i*28}V278`,C.slate,1.4);}
v+=duct(65,250,305,22)+rect(85,282,251,63,C.light,C.navy,1.6);
for(let i=0;i<8;i++){v+=rect(97+i*29,293,18,15,C.white,C.navy,1.1)+screw(106+i*29,326)+path(`M${106+i*29} 346V${364+(i%2)*8}H${98+i*31}V406`,C.slate,1.25);}
v+=terminalRow(84,404,8,32);
for(let i=0;i<8;i++)v+=line(99+i*32,442,99+i*32,474,C.slate,1.2);
// Checkpoint ring and leaders deliberately mark geometry without claiming a passed test.
for(const [num,x,y,endY] of [[1,247,241,151],[2,222,326,279],[3,325,422,407]]){
 v+=circle(x,y,8,C.white,C.navy,1.5)+circle(x,y,3,C.yellow,'none',0)+path(`M${x+8} ${y}H${374+num*4}V${endY}H416`,C.slate,1.3);
 v+=circle(430,endY,15,C.yellow,C.navy,1.3)+text(430,endY+6,String(num),18,'middle',C.navy,600);
}
v+=text(460,157,'Cablaggi',20,'start',C.navy,600)+text(460,285,'Serraggi',20,'start',C.navy,600)+text(460,413,'Connessioni',20,'start',C.navy,600);
// Modest checklist lines — unfilled boxes represent checkpoints to inspect.
for(const y of [182,310,438])v+=rect(460,y,14,14,C.white,C.slate,1.2)+line(486,y+7,580,y+7,C.slate,1.2);
v+=text(320,537,'Riscontro visivo e verifica dei collegamenti',18,'middle',C.slate);
await writeFile(resolve(out,'inspection.svg'),svg('industrial-inspection','Schema illustrativo dei punti di verifica di un quadro','Tre punti numerati indicano cablaggi, serraggi e connessioni. Riquadri vuoti richiamano una lista di controllo, senza dichiarare un esito o una certificazione.',v));
console.log(JSON.stringify({panel:resolve(out,'panel.svg'),automation:resolve(out,'automation.svg'),inspection:resolve(out,'inspection.svg')},null,2));
