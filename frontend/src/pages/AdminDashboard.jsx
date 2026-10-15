import React from "react";
import { BarChart3, ClipboardList, Pill, ShieldCheck, Users } from "lucide-react";
import { PageHeader, StatCard } from "../components/UI";

export default function AdminDashboard(){
 return <><PageHeader eyebrow="ADMINISTRATION" title="System overview" description="The backend currently exposes role protection for admin routes; dedicated admin controllers are not included in the supplied project."/><div className="stats-grid"><StatCard label="User management" value="Ready" hint="API route reserved" icon={Users}/><StatCard label="Medicine monitoring" value="Ready" hint="API route reserved" icon={Pill}/><StatCard label="Order monitoring" value="Ready" hint="API route reserved" icon={ClipboardList}/><StatCard label="Security" value="JWT" hint="Role protected" icon={ShieldCheck}/></div><div className="admin-note"><BarChart3 size={22}/><div><h3>Admin UI scaffold</h3><p>Your backend ZIP contains the ADMIN security rules but no <code>AdminController</code> or admin DTO/service endpoints yet. This page is intentionally kept as a clean placeholder so the frontend can be extended as soon as those APIs are added.</p></div></div></>;
}