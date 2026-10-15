import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ArrowRight, Building2, MapPin, Mail, Phone, UserRound, LockKeyhole, Pill } from "lucide-react";
import { authApi } from "../api";
import { Spinner } from "../components/UI";
import { useAuth } from "../auth";

const initial = { name:"", email:"", password:"", phone:"", businessName:"", address:"", role:"SHOP_OWNER" };

export default function Register() {
  const { user, login } = useAuth();
  const navigate = useNavigate();
  const [form,setForm]=useState(initial);
  const [busy,setBusy]=useState(false);
  const [error,setError]=useState("");
  if(user){ navigate("/",{replace:true}); return null; }

  const set=(key,value)=>setForm({...form,[key]:value});
  const submit=async(e)=>{
    e.preventDefault(); setBusy(true); setError("");
    try {
      const {data}=await authApi.register(form);
      // Register endpoint returns UserResponse, so send the user to login.
      navigate("/login",{state:{registered:true}});
    } catch(err) {
      setError(err.response?.data?.message || "Registration failed. Please check the details and try again.");
    } finally { setBusy(false); }
  };

  return <div className="auth-page register-page">
    <div className="auth-visual">
      <div className="auth-brand"><div className="brand-mark"><Pill size={21}/></div><b>Med<span>Links</span></b></div>
      <div className="auth-hero"><span className="kicker">JOIN THE NETWORK</span><h1>Build a stronger<br/><em>medicine chain.</em></h1><p>Manage stock, discover medicines, and move orders from shelf to shop with clarity.</p></div>
      <div className="role-preview"><div><b>SHOP OWNER</b><span>Browse · Cart · Orders</span></div><div><b>STOCK SELLER</b><span>Inventory · Fulfilment</span></div></div>
    </div>
    <div className="auth-panel register-panel">
      <div className="auth-form-wrap wide">
        <div className="mobile-auth-logo"><div className="brand-mark"><Pill size={19}/></div><b>Med<span>Links</span></b></div>
        <div className="form-head"><span className="eyebrow">GET STARTED</span><h2>Create your account</h2><p>Choose how you work with the MedLinks network.</p></div>
        {error && <div className="form-error">{error}</div>}
        <form onSubmit={submit} className="register-form">
          <div className="role-select">
            <button type="button" className={form.role==="SHOP_OWNER"?"selected":""} onClick={()=>set("role","SHOP_OWNER")}><Building2 size={18}/><span><b>Shop Owner</b><small>Buy medicine for your shop</small></span></button>
            <button type="button" className={form.role==="WHOLESALER"?"selected":""} onClick={()=>set("role","WHOLESALER")}><Pill size={18}/><span><b>Stock Seller</b><small>List and manage inventory</small></span></button>
          </div>
          <div className="form-grid">
            <Field icon={UserRound} label="Full name" value={form.name} onChange={v=>set("name",v)} placeholder="Your name" required/>
            <Field icon={Mail} label="Email" value={form.email} onChange={v=>set("email",v)} placeholder="you@business.com" type="email" required/>
            <Field icon={Phone} label="Phone" value={form.phone} onChange={v=>set("phone",v)} placeholder="+91 98765 43210"/>
            <Field icon={LockKeyhole} label="Password" value={form.password} onChange={v=>set("password",v)} placeholder="Create a password" type="password" required/>
            <Field icon={Building2} label="Business name" value={form.businessName} onChange={v=>set("businessName",v)} placeholder="Business / shop name" wide/>
            <Field icon={MapPin} label="Address" value={form.address} onChange={v=>set("address",v)} placeholder="Village, district, state" wide/>
          </div>
          <button className="auth-submit" disabled={busy}>{busy?<Spinner/>:<>Create account <ArrowRight size={17}/></>}</button>
        </form>
        <p className="switch-auth">Already have an account? <Link to="/login">Sign in</Link></p>
      </div>
    </div>
  </div>;
}

function Field({icon:Icon,label,value,onChange,placeholder,type="text",required,wide}) {
  return <label className={wide?"grid-wide":""}>{label}<div className="input-icon"><Icon size={16}/><input type={type} value={value} onChange={e=>onChange(e.target.value)} placeholder={placeholder} required={required}/></div></label>;
}