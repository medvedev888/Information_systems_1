import { createRouter, createWebHistory } from 'vue-router'
import Home from '../pages/Home.vue'
import Registration from '../pages/Registration.vue'
import Login from '../pages/Login.vue'
import SpecialOperations from '../pages/SpecialOperations.vue'

const routes = [
  { path: "/", name: "Home", component: Home, meta: { requiresAuth: true } },
  {path: "/registration", name: "Registration", component: Registration},
  {path: "/login", name: "Login", component: Login},
  {path: "/operations", name: "SpecialOperations", component: SpecialOperations, meta: { requiresAuth: true }}
]

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
