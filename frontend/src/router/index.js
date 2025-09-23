import { createRouter, createWebHistory } from 'vue-router'
import Home from '../pages/Home.vue'
import Register from '../pages/Register.vue'
import Login from '../pages/Login.vue'
import SpecialOperations from '../pages/SpecialOperations.vue'

const routes = [
  {path: "/", name: "Home", component: Home},
  {path: "/register", name: "Register", component: Register},
  {path: "/login", name: "Login", component: Login},
  {path: "/operations", name: "SpecialOperations", component: SpecialOperations}
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router
