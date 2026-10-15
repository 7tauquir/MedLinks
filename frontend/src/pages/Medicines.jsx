import React, { useEffect, useMemo, useState } from "react";
import { Filter, Plus, Search, SlidersHorizontal, X } from "lucide-react";
import { medicineApi, cartApi } from "../api";
import { useAuth } from "../auth";
import { PageHeader, MedicineCard, Spinner, ErrorBox, Modal, SuccessToast } from "../components/UI";

const cats=["All","Pain Relief","Antibiotic","Gastro","Vitamin","Cold & Flu","Diabetes","Other"];

export default function Medicines(){
 const {user}=useAuth(); const seller=user.role==="WHOLESALER";
 const [meds,setMeds]=useState([]),[query,setQuery]=useState(""),[cat,setCat]=useState("All"),[loading,setLoading]=useState(true),[error,setError]=useState(""),[toast,setToast]=useState(""),[busy,setBusy]=useState(null),[modal,setModal]=useState(false);
 const load=async()=>{setLoading(true);setError("");try{const {data}=seller?await medicineApi.seller(user.email):await medicineApi.all();setMeds(data||[])}catch(e){setError(e.response?.data?.message||"Could not load medicines.")}finally{setLoading(false)}};
 useEffect(()=>{load()},[user.email,user.role]);
 const filtered=useMemo(()=>meds.filter(m=>(m.name||"").toLowerCase().includes(query.toLowerCase())&&(cat==="All"||m.category===cat)),[meds,query,cat]);
 const add=async(m)=>{setBusy(m.id);try{await cartApi.add(m.id,1,user.email);setToast(`${m.name} added to cart`)}catch(e){setToast(e.response?.data?.message||"Could not add to cart.")}finally{setBusy(null)}};
 return <><PageHeader eyebrow={seller?"INVENTORY":"MEDICINE CATALOG"} title={seller?"Manage your inventory":"Find the right medicine"} description={seller?"Keep stock, pricing and batches ready for your shop network.":"Browse verified medicine inventory from MedLinks stock sellers."} action={seller&&<button className="btn primary" onClick={()=>setModal(true)}><Plus size={16}/> Add medicine</button>}/>
 <div className="catalog-toolbar"><div className="catalog-search"><Search size={17}/><input value={query} onChange={e=>setQuery(e.target.value)} placeholder="Search by medicine name..."/>{query&&<button onClick={()=>setQuery("")}><X size={15}/></button>}</div><button className="filter-btn"><SlidersHorizontal size={16}/> Filters</button></div>
 <div className="category-row">{cats.map(x=><button key={x} className={cat===x?"chip active":"chip"} onClick={()=>setCat(x)}>{x}</button>)}</div>
 {error&&<ErrorBox message={error} onRetry={load}/>}
 {loading?<div className="loading-row tall"><Spinner/> Loading medicine catalog...</div>:filtered.length?<div className="medicine-grid catalog-grid">{filtered.map(m=><MedicineCard key={m.id} medicine={m} onAdd={seller?null:add} busy={busy===m.id}/>)}</div>:<div className="empty-state"><Filter size={25}/><h3>No medicines found</h3><p>Try a different search term or category.</p></div>}
 {seller&&<MedicineModal open={modal} onClose={()=>setModal(false)} email={user.email} onSaved={()=>{setModal(false);load();setToast("Medicine added to inventory")}}/>}
 <SuccessToast text={toast} onClose={()=>setToast("")}/>
 </>;
}

function MedicineModal({open,onClose,email,onSaved}) {
 const [form,setForm]=useState({name:"",category:"",manufacturer:"",price:"",stock:"",batchNumber:"",expiryDate:"",prescriptionRequired:false}),[busy,setBusy]=useState(false),[error,setError]=useState("");
 const set=(k,v)=>setForm({...form,[k]:v});
 const submit=async(e)=>{e.preventDefault();setBusy(true);setError("");try{await medicineApi.create({...form,price:Number(form.price),stock:Number(form.stock)},email);onSaved()}catch(err){setError(err.response?.data?.message||"Could not create medicine.")}finally{setBusy(false)}};
 return <Modal open={open} title="Add medicine to inventory" onClose={onClose} footer={<><button className="btn ghost" onClick={onClose}>Cancel</button><button className="btn primary" onClick={submit} disabled={busy}>{busy?<Spinner/>:"Add medicine"}</button></>}>
 {error&&<div className="form-error">{error}</div>}<form className="form-grid modal-form" onSubmit={submit}>
 <Field label="Medicine name" value={form.name} set={v=>set("name",v)} required/><Field label="Category" value={form.category} set={v=>set("category",v)} placeholder="Pain Relief" required/>
 <Field label="Manufacturer" value={form.manufacturer} set={v=>set("manufacturer",v)}/><Field label="Price (₹)" value={form.price} set={v=>set("price",v)} type="number" required/>
 <Field label="Stock" value={form.stock} set={v=>set("stock",v)} type="number" required/><Field label="Batch number" value={form.batchNumber} set={v=>set("batchNumber",v)}/>
 <Field label="Expiry date" value={form.expiryDate} set={v=>set("expiryDate",v)} placeholder="YYYY-MM-DD"/>
 <label className="check-row"><input type="checkbox" checked={form.prescriptionRequired} onChange={e=>set("prescriptionRequired",e.target.checked)}/> Prescription required</label>
 </form></Modal>
}
function Field({label,value,set,type="text",placeholder,required}){return <label>{label}<input className="plain-input" type={type} value={value} onChange={e=>set(e.target.value)} placeholder={placeholder} required={required}/></label>}
