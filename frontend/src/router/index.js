import {createRouter, createWebHistory} from 'vue-router'
import Home from '../pages/Organizations.vue'
import Registration from '../pages/Registration.vue'
import Login from '../pages/Login.vue'
import SpecialOperations from '../pages/SpecialOperations.vue'
import Addresses from "@/pages/Addresses.vue";
import Locations from "@/pages/Locations.vue";
import Coordinates from "@/pages/Coordinates.vue";

const routes = [
  { path: "/registration", name: "Registration", component: Registration },
  { path: "/login", name: "Login", component: Login },

  { path: "/", name: "Organizations", component: Home, meta: { requiresAuth: true } },

  { path: "/locations", name: "Locations", component: Locations, meta: { requiresAuth: true } },
  { path: "/addresses", name: "Addresses", component: Addresses, meta: { requiresAuth: true } },
  { path: "/coordinates", name: "Coordinates", component: Coordinates, meta: { requiresAuth: true } },

  { path: "/operations", name: "SpecialOperations", component: SpecialOperations, meta: { requiresAuth: true } }
];


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("jwt");
  if (to.meta.requiresAuth && !token) {
    next("/login");
  } else {
    next();
  }
});

export default router
