import React, { useEffect, useState } from "react";
import { ArrowUpRight, ClipboardList, PackageCheck, Pill, ShoppingCart, Sparkles, Truck } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { cartApi, medicineApi, orderApi } from "../api";
import { useAuth } from "../auth";
import { PageHeader, StatCard, MedicineCard, Spinner, ErrorBox, LinkArrow, SuccessToast } from "../components/UI";

export default function ShopDashboard() {
  const {user}=useAuth(); const nav=useNavigate();
  const [meds,setMeds]=useState([]); const [orders,setOrders]=useState([]); const [cart,setCart]=useState(null);
  const [loading,setLoading]=useState(true); const [error,setError]=useState(""); const [toast,setToast]=useState("");
  useEffect(()=>{(async()=>{try{const [m,o,c]=await Promise.all([medicineApi.all(),orderApi.mine(user.email),cartApi.get(user.email)]);setMeds(m.data||[]);setOrders(o.data||[]);setCart(c.data)}catch(e){setError(e.response?.data?.message||"Could not load dashboard.")}finally{setLoading(false)}})()},[user.email]);
  const add=async(m)=>{try{await cartApi.add(m.id,1,user.email);setToast(`${m.name} added to cart`);const {data}=await cartApi.get(user.email);setCart(data)}catch(e){setToast(e.response?.data?.message||"Could not add medicine.")}};
  const recent=orders.slice(0,4);
  return <><PageHeader eyebrow="SHOP OWNER" title={`Good to see you, ${user.name || "there"}.`} description="Keep your shelves ready with a clear view of medicines, cart and orders." action={<button className="btn primary" onClick={()=>nav("/medicines")}><Pill size={16}/> Browse medicines</button>}/>
  {error&&<ErrorBox message={error}/>}
  <div className="stats-grid">
    <StatCard label="Available medicines" value={loading?"—":meds.length} hint="Across the network" icon={Pill}/>
    <StatCard label="Cart items" value={loading?"—":(cart?.items||[]).reduce((s,x)=>s+(x.quantity||0),0)} hint="Ready for checkout" icon={ShoppingCart}/>
    <StatCard label="Orders placed" value={loading?"—":orders.length} hint="Your order history" icon={ClipboardList}/>
    <StatCard label="In transit" value={loading?"—":orders.filter(x=>["SHIPPED","PACKED"].includes(x.status)).length} hint="Currently moving" icon={Truck}/>
  </div>
  <section className="section">
    <div className="section-head"><div><h2>Recommended medicines</h2><p>Popular products from verified stock sellers.</p></div><LinkArrow onClick={()=>nav("/medicines")}>View all</LinkArrow></div>
    {loading?<div className="loading-row"><Spinner/> Loading medicines...</div>:<div className="medicine-grid">{meds.slice(0,4).map(m=><MedicineCard key={m.id} medicine={m} onAdd={add}/>)}</div>}
  </section>
  <section className="section">
    <div className="section-head"><div><h2>Recent orders</h2><p>Track your latest medicine purchases.</p></div><LinkArrow onClick={()=>nav("/orders")}>All orders</LinkArrow></div>
    <OrderPreview orders={recent} loading={loading}/>
  </section>
  <SuccessToast text={toast} onClose={()=>setToast("")}/>
  </>;
}

function OrderPreview({orders,loading}) {
  if(loading)return <div className="loading-row"><Spinner/> Loading orders...</div>;
  if(!orders.length)return <div className="panel empty-inline"><PackageCheck size={21}/><span>No orders yet. Your next restock starts here.</span></div>;
  return <div className="panel table-wrap"><table><thead><tr><th>Order</th><th>Items</th><th>Amount</th><th>Status</th><th></th></tr></thead><tbody>{orders.map(o=><tr key={o.orderId}><td><b>#{String(o.orderId).padStart(5,"0")}</b></td><td>{o.items?.length||0} products</td><td>₹{Number(o.totalAmount||0).toLocaleString("en-IN")}</td><td><span className={`status ${(o.status||"").toLowerCase()}`}><span/>{o.status}</span></td><td><button className="table-link">View</button></td></tr>)}</tbody></table></div>;
}