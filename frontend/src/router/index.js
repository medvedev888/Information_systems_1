import {createRouter, createWebHistory} from 'vue-router'
import Home from '../pages/Organizations.vue'
import Registration from '../pages/Registration.vue'
import Login from '../pages/Login.vue'
import SpecialOperations from '../pages/SpecialOperations.vue'
import Addresses from "@/pages/Addresses.vue";
import Locations from "@/pages/Locations.vue";
import Coordinates from "@/pages/Coordinates.vue";
import ImportHistory from "@/pages/ImportHistory.vue";
import {useAuthStore} from "@/stores/authStore.js";
import ForbiddenPage from "@/pages/ForbiddenPage.vue";
import AdminPanel from "@/pages/AdminPanel.vue";

const routes = [
  { path: "/registration", name: "Registration", component: Registration },
  { path: "/login", name: "Login", component: Login },

  { path: "/", name: "Organizations", component: Home, meta: { requiresAuth: true, requiresAdmin: false } },

  { path: "/locations", name: "Locations", component: Locations, meta: { requiresAuth: true, requiresAdmin: false  } },
  { path: "/addresses", name: "Addresses", component: Addresses, meta: { requiresAuth: true, requiresAdmin: false  } },
  { path: "/coordinates", name: "Coordinates", component: Coordinates, meta: { requiresAuth: true, requiresAdmin: false  } },

  { path: "/operations", name: "SpecialOperations", component: SpecialOperations, meta: { requiresAuth: true, requiresAdmin: false  } },
  { path: "/import-history", name: "ImportHistory", component: ImportHistory, meta: { requiresAuth: true, requiresAdmin: false  } },
  { path: "/admin-panel", name: "AdminPanel", component: AdminPanel, meta: { requiresAuth: true, requiresAdmin: true } },

  { path: "/forbidden", name: "Forbidden", component: ForbiddenPage }

];


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

router.beforeEach(async (to, from, next) => {
  const token = localStorage.getItem('jwt');
  const auth = useAuthStore();

  // если нет токена и нужна авторизация
  if (to.meta.requiresAuth && !token) {
    return next('/login');
  }

  // если есть токен, но мы ещё не знаем user
  if (token && !auth.user) {
    try {
      await auth.fetchMe();
    } catch (e) {
      localStorage.removeItem("jwt");
      return next("/login");
    }
  }

  // проверка ролей
  if (to.meta.requiresAdmin && auth.user?.role !== 'ADMIN') {
    return next('/forbidden');
  }

  next();
});

export default router
