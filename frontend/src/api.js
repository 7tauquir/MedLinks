import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "/api",
  headers: { "Content-Type": "application/json" }
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("medlinks_token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("medlinks_token");
      localStorage.removeItem("medlinks_user");
    }
    return Promise.reject(error);
  }
);

export default api;

export const authApi = {
  login: (payload) => api.post("/auth/login", payload),
  register: (payload) => api.post("/auth/register", payload)
};

export const medicineApi = {
  all: () => api.get("/medicines"),
  byId: (id) => api.get(`/medicines/${id}`),
  search: (name) => api.get("/medicines/search", { params: { name } }),
  category: (category) => api.get("/medicines/category", { params: { category } }),
  seller: (email) => api.get("/medicines/seller", { params: { sellerEmail: email } }),
  create: (payload, email) => api.post("/medicines", payload, { params: { sellerEmail: email } }),
  update: (id, payload, email) => api.put(`/medicines/${id}`, payload, { params: { sellerEmail: email } }),
  remove: (id, email) => api.delete(`/medicines/${id}`, { params: { sellerEmail: email } })
};

export const cartApi = {
  get: (email) => api.get("/cart", { params: { shopOwnerEmail: email } }),
  add: (medicineId, quantity, email) =>
    api.post("/cart/add", { medicineId, quantity }, { params: { shopOwnerEmail: email } }),
  update: (medicineId, quantity, email) =>
    api.put(`/cart/update/${medicineId}`, null, { params: { shopOwnerEmail: email, quantity } }),
  remove: (medicineId, email) =>
    api.delete(`/cart/remove/${medicineId}`, { params: { shopOwnerEmail: email } }),
  clear: (email) => api.delete("/cart/clear", { params: { shopOwnerEmail: email } })
};

export const orderApi = {
  place: (deliveryAddress, email) =>
    api.post("/orders", { deliveryAddress }, { params: { shopOwnerEmail: email } }),
  mine: (email) => api.get("/orders/my", { params: { shopOwnerEmail: email } }),
  seller: (email) => api.get("/orders/wholesaler", { params: { wholesalerEmail: email } }),
  byId: (id, email) => api.get(`/orders/${id}`, { params: { userEmail: email } }),
  status: (id, status, email) =>
    api.put(`/orders/${id}/status`, null, { params: { status, wholesalerEmail: email } }),
  cancel: (id, email) =>
    api.put(`/orders/${id}/cancel`, null, { params: { shopOwnerEmail: email } })
};