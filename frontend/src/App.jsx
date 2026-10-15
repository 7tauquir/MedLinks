import React from "react";
import { Navigate, Route, Routes } from "react-router-dom";
import { AuthProvider, useAuth } from "./auth";
import Layout from "./components/Layout";
import Login from "./pages/Login";
import Register from "./pages/Register";
import ShopDashboard from "./pages/ShopDashboard";
import Medicines from "./pages/Medicines";
import MedicineDetails from "./pages/MedicineDetails";
import Cart from "./pages/Cart";
import Orders from "./pages/Orders";
import SellerDashboard from "./pages/SellerDashboard";
import AdminDashboard from "./pages/AdminDashboard";
import NotFound from "./pages/NotFound";

function Protected({ children, roles }) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  if (roles && !roles.includes(user.role)) return <Navigate to="/" replace />;
  return children;
}

function Home() {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  if (user.role === "WHOLESALER") return <Navigate to="/seller" replace />;
  if (user.role === "ADMIN") return <Navigate to="/admin" replace />;
  return <Navigate to="/dashboard" replace />;
}

export default function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route element={<Layout />}>
          <Route path="/" element={<Home />} />
          <Route path="/dashboard" element={<Protected roles={["SHOP_OWNER"]}><ShopDashboard /></Protected>} />
          <Route path="/medicines" element={<Protected><Medicines /></Protected>} />
          <Route path="/medicines/:id" element={<Protected><MedicineDetails /></Protected>} />
          <Route path="/cart" element={<Protected roles={["SHOP_OWNER"]}><Cart /></Protected>} />
          <Route path="/orders" element={<Protected roles={["SHOP_OWNER"]}><Orders /></Protected>} />
          <Route path="/seller" element={<Protected roles={["WHOLESALER"]}><SellerDashboard /></Protected>} />
          <Route path="/admin" element={<Protected roles={["ADMIN"]}><AdminDashboard /></Protected>} />
          <Route path="*" element={<NotFound />} />
        </Route>
      </Routes>
    </AuthProvider>
  );
}