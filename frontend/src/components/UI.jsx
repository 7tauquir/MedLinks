import React from "react";
import { ArrowRight, Check, LoaderCircle, Minus, Plus, RefreshCcw, ShoppingCart, X } from "lucide-react";

export function PageHeader({ eyebrow, title, description, action }) {
  return <div className="page-header">
    <div><div className="eyebrow">{eyebrow}</div><h1>{title}</h1>{description && <p>{description}</p>}</div>
    {action}
  </div>;
}

export function StatCard({ label, value, hint, icon: Icon }) {
  return <div className="stat-card"><div className="stat-top"><span>{label}</span><div className="stat-icon"><Icon size={18}/></div></div><strong>{value}</strong>{hint && <small>{hint}</small>}</div>;
}

export function StatusBadge({ status }) {
  const s = (status || "PLACED").toLowerCase();
  return <span className={`status ${s}`}><span />{status || "PLACED"}</span>;
}

export function EmptyState({ icon: Icon = ShoppingCart, title, text, action }) {
  return <div className="empty-state"><div className="empty-icon"><Icon size={24}/></div><h3>{title}</h3><p>{text}</p>{action}</div>;
}

export function Spinner() { return <LoaderCircle className="spin" size={19}/>; }

export function ErrorBox({ message, onRetry }) {
  return <div className="error-box"><X size={18}/><span>{message || "Something went wrong."}</span>{onRetry && <button onClick={onRetry}><RefreshCcw size={15}/> Retry</button>}</div>;
}

export function Modal({ open, title, children, onClose, footer }) {
  if (!open) return null;
  return <div className="modal-backdrop" onMouseDown={onClose}><div className="modal" onMouseDown={e => e.stopPropagation()}><div className="modal-head"><h3>{title}</h3><button className="icon-btn" onClick={onClose}><X size={18}/></button></div><div className="modal-body">{children}</div>{footer && <div className="modal-foot">{footer}</div>}</div></div>;
}

export function Quantity({ value, onChange, min=1 }) {
  return <div className="quantity"><button onClick={() => onChange(Math.max(min, value - 1))}><Minus size={14}/></button><b>{value}</b><button onClick={() => onChange(value + 1)}><Plus size={14}/></button></div>;
}

export function MedicineCard({ medicine, onAdd, busy }) {
  const stock = Number(medicine.stock || 0);
  return <article className="medicine-card">
    <div className="medicine-art"><PillArt category={medicine.category}/>{medicine.prescriptionRequired && <span className="rx">Rx</span>}</div>
    <div className="medicine-body">
      <div className="medicine-category">{medicine.category || "Medicine"}</div>
      <h3>{medicine.name}</h3>
      <p>{medicine.manufacturer || "Verified manufacturer"}</p>
      <div className="medicine-meta"><b>₹{Number(medicine.price || 0).toLocaleString("en-IN")}</b><span className={stock < 10 ? "low-stock" : ""}>{stock} in stock</span></div>
      <div className="seller-line"><span className="seller-avatar">{(medicine.sellerBusinessName || medicine.sellerName || "S").slice(0,1)}</span><span>{medicine.sellerBusinessName || medicine.sellerName || "Verified seller"}</span></div>
      {onAdd && <button className="primary full" disabled={!stock || busy} onClick={() => onAdd(medicine)}>{busy ? <Spinner/> : <ShoppingCart size={16}/>} {stock ? "Add to cart" : "Out of stock"}</button>}
    </div>
  </article>;
}

function PillArt({ category }) {
  const letters = (category || "MED").slice(0, 3).toUpperCase();
  return <div className="pill-art"><span className="pill-shape"><i></i></span><b>{letters}</b></div>;
}

export function Button({ children, variant="primary", ...props }) {
  return <button className={`btn ${variant}`} {...props}>{children}</button>;
}

export function SuccessToast({ text, onClose }) {
  if (!text) return null;
  return <div className="toast"><Check size={17}/><span>{text}</span><button onClick={onClose}><X size={15}/></button></div>;
}

export function LinkArrow({ children, onClick }) {
  return <button className="link-arrow" onClick={onClick}>{children}<ArrowRight size={15}/></button>;
}