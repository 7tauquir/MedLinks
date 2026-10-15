import React, { createContext, useContext, useMemo, useState } from "react";

const AuthContext = createContext(null);

const readUser = () => {
  try {
    return JSON.parse(localStorage.getItem("medlinks_user") || "null");
  } catch {
    return null;
  }
};

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readUser);

  const login = (data) => {
    const next = {
      email: data.email,
      role: data.role,
      name: data.name || data.email?.split("@")[0] || "User"
    };
    localStorage.setItem("medlinks_token", data.token);
    localStorage.setItem("medlinks_user", JSON.stringify(next));
    setUser(next);
  };

  const logout = () => {
    localStorage.removeItem("medlinks_token");
    localStorage.removeItem("medlinks_user");
    setUser(null);
  };

  const value = useMemo(() => ({ user, login, logout }), [user]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);