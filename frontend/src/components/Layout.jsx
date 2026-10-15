import React from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { Bell, Boxes, ClipboardList, LayoutDashboard, LogOut, Menu, Pill, Search, ShoppingCart, X } from "lucide-react";
import { useAuth } from "../auth";

export default function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [open, setOpen] = React.useState(false);

  if (!user) return <Outlet />;

  const role = user.role;
  const links =
    role === "SHOP_OWNER"
      ? [
          ["/dashboard", "Overview", LayoutDashboard],
          ["/medicines", "Medicines", Pill],
          ["/cart", "Cart", ShoppingCart],
          ["/orders", "Orders", ClipboardList]
        ]
      : role === "WHOLESALER"
        ? [
            ["/seller", "Dashboard", LayoutDashboard],
            ["/medicines", "Inventory", Boxes],
            ["/orders", "Orders", ClipboardList]
          ]
        : [["/admin", "Dashboard", LayoutDashboard]];

  const go = (path) => {
    setOpen(false);
    navigate(path);
  };

  return (
    <div className="app-shell">
      <aside className={`sidebar ${open ? "sidebar-open" : ""}`}>
        <div className="brand">
          <div className="brand-mark"><Pill size={20} /></div>
          <div><strong>Med<span>Links</span></strong><small>B2B medicine network</small></div>
          <button className="icon-btn mobile-close" onClick={() => setOpen(false)}><X size={19} /></button>
        </div>

        <div className="workspace-label">WORKSPACE</div>
        <nav className="nav">
          {links.map(([path, label, Icon]) => (
            <NavLink key={path} to={path} onClick={() => setOpen(false)} className={({isActive}) => isActive ? "nav-item active" : "nav-item"}>
              <Icon size={18} /> <span>{label}</span>
            </NavLink>
          ))}
        </nav>

        <div className="sidebar-bottom">
          <div className="support-card">
            <div className="support-icon"><Bell size={16} /></div>
            <div><b>Need help?</b><span>Contact MedLinks support</span></div>
          </div>
          <button className="profile-mini" onClick={() => go("/dashboard")}>
            <div className="avatar">{(user.name || user.email).slice(0,1).toUpperCase()}</div>
            <div className="profile-copy"><b>{user.name || "Account"}</b><span>{role === "WHOLESALER" ? "Stock Seller" : role === "SHOP_OWNER" ? "Shop Owner" : "Administrator"}</span></div>
            <span className="profile-dot" />
          </button>
          <button className="logout-btn" onClick={() => { logout(); navigate("/login"); }}><LogOut size={16} /> Sign out</button>
        </div>
      </aside>

      <main className="main">
        <header className="topbar">
          <button className="icon-btn mobile-menu" onClick={() => setOpen(true)}><Menu size={20} /></button>
          <div className="top-search"><Search size={17} /><span>Search medicines, orders...</span></div>
          <div className="top-actions">
            <button className="icon-btn"><Bell size={18} /></button>
            <div className="top-user"><div className="avatar small">{(user.name || user.email).slice(0,1).toUpperCase()}</div><span>{user.name || "Account"}</span></div>
          </div>
        </header>
        <div className="page-wrap"><Outlet /></div>
      </main>
    </div>
  );
}