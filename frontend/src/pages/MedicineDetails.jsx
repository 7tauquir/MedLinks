import React, { useEffect, useState } from "react";
import { ArrowLeft, Building2, CalendarDays, Package, Pill, ShieldCheck, ShoppingCart } from "lucide-react";
import { useNavigate, useParams } from "react-router-dom";
import { cartApi, medicineApi } from "../api";
import { useAuth } from "../auth";
import { Button, ErrorBox, Spinner, SuccessToast } from "../components/UI";

export default function MedicineDetails(){
 const {id}=useParams();const nav=useNavigate();const {user}=useAuth();const [m,setM]=useState(null),[loading,setLoading]=useState(true),[error,setError]=useState(""),[toast,setToast]=useState("");
 useEffect(()=>{medicineApi.byId(id).then(r=>setM(r.data)).catch(e=>setError(e.response?.data?.message||"Medicine not found.")).finally(()=>setLoading(false))},[id]);
 const add=async()=>{try{await cartApi.add(m.id,1,user.email);setToast(`${m.name} added to cart`)}catch(e){setToast(e.response?.data?.message||"Could not add to cart.")}};
 if(loading)return <div className="loading-row tall"><Spinner/> Loading medicine...</div>;
 if(error)return <ErrorBox message={error} onRetry={()=>location.reload()}/>;
 return <><button className="back-link" onClick={()=>nav("/medicines")}><ArrowLeft size={16}/> Back to medicines</button><div className="detail-layout">
 <div className="detail-art"><div className="detail-pill"><Pill size={74}/></div><span>MEDLINKS VERIFIED</span></div>
 <div className="detail-content"><div className="eyebrow">{m.category||"MEDICINE"}</div><h1>{m.name}</h1><p className="detail-sub">{m.manufacturer||"Verified manufacturer"}</p><div className="price-big">₹{Number(m.price||0).toLocaleString("en-IN")} <small>per unit</small></div>
 <div className="detail-grid"><Info icon={Package} label="Stock" value={`${m.stock||0} units`}/><Info icon={Building2} label="Seller" value={m.sellerBusinessName||m.sellerName||"Verified seller"}/><Info icon={CalendarDays} label="Expiry" value={m.expiryDate||"—"}/><Info icon={ShieldCheck} label="Batch" value={m.batchNumber||"—"}/></div>
 <div className="detail-note"><ShieldCheck size={18}/><span>Medicine details are supplied by the registered stock seller. Verify local prescription requirements before dispensing.</span></div>
 {user.role==="SHOP_OWNER"&&<Button onClick={add}><ShoppingCart size={17}/> Add to cart</Button>}</div></div><SuccessToast text={toast} onClose={()=>setToast("")}/></>;
}
function Info({icon:Icon,label,value}){return <div className="info-item"><Icon size={17}/><div><small>{label}</small><b>{value}</b></div></div>}
