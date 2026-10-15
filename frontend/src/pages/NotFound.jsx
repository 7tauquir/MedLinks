import React from "react";
import { Link } from "react-router-dom";
export default function NotFound(){return <div className="empty-state"><h1>404</h1><h3>Page not found</h3><p>The page you're looking for doesn't exist.</p><Link className="btn primary" to="/">Go home</Link></div>}