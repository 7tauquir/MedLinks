import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ArrowRight, Eye, EyeOff, LockKeyhole, Mail, Pill, ShieldCheck } from "lucide-react";
import { authApi } from "../api";
import { useAuth } from "../auth";
import { Spinner } from "../components/UI";

export default function Login() {
  const { user, login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: "", password: "" });
  const [show, setShow] = useState(false);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");

  if (user) {
    navigate("/", { replace: true });
    return null;
  }

  const submit = async (e) => {
    e.preventDefault();
    setBusy(true); setError("");
    try {
      const { data } = await authApi.login(form);
      login(data);
      navigate("/");
    } catch (err) {
      setError(err.response?.data?.message || "Unable to sign in. Check your email and password.");
    } finally { setBusy(false); }
  };

  return <div className="auth-page">
    <div className="auth-visual">
      <div className="auth-brand"><div className="brand-mark"><Pill size={21}/></div><b>Med<span>Links</span></b></div>
      <div className="auth-hero">
        <span className="kicker">B2B MEDICINE SUPPLY</span>
        <h1>Better stock.<br/><em>Better care.</em></h1>
        <p>One trusted network connecting medicine stock sellers with local shops across villages and semi-urban communities.</p>
        <div className="trust-row"><ShieldCheck size={17}/><span>Secure JWT authentication</span><span className="sep">•</span><span>Verified supply network</span></div>
      </div>
      <div className="auth-quote">“Simple enough for a busy shop. Powerful enough for a growing supply network.”</div>
    </div>
    <div className="auth-panel">
      <div className="auth-form-wrap">
        <div className="mobile-auth-logo"><div className="brand-mark"><Pill size={19}/></div><b>Med<span>Links</span></b></div>
        <div className="form-head"><span className="eyebrow">WELCOME BACK</span><h2>Sign in to MedLinks</h2><p>Access your medicine supply workspace.</p></div>
        {error && <div className="form-error">{error}</div>}
        <form onSubmit={submit}>
          <label>Email address</label>
          <div className="input-icon"><Mail size={17}/><input type="email" required value={form.email} onChange={e => setForm({...form,email:e.target.value})} placeholder="you@business.com"/></div>
          <label>Password</label>
          <div className="input-icon"><LockKeyhole size={17}/><input type={show ? "text" : "password"} required value={form.password} onChange={e => setForm({...form,password:e.target.value})} placeholder="Enter your password"/><button type="button" onClick={() => setShow(!show)}>{show ? <EyeOff size={17}/> : <Eye size={17}/>}</button></div>
          <button className="auth-submit" disabled={busy}>{busy ? <Spinner/> : <>Sign in <ArrowRight size={17}/></>}</button>
        </form>
        <p className="switch-auth">New to MedLinks? <Link to="/register">Create an account</Link></p>
      </div>
      <div className="auth-footer">© 2026 MedLinks <span>•</span> B2B healthcare supply platform</div>
    </div>
  </div>;
}