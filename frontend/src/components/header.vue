<script setup>
import {ref, watch} from "vue";
import {useRoute, useRouter} from "vue-router";
import Button from "@/components/button.vue";
import Link from "@/components/link.vue";
import {useAuthStore} from "@/stores/authStore.js";
import { computed } from 'vue';


const router = useRouter();
const route = useRoute();
const userLogin = ref("Guest");
const auth = useAuthStore();

function logout() {
  localStorage.removeItem("jwt");
  router.push("/login");
}

function isActive(href) {
  return route.path === href;
}

const menuItems = [
  {label: 'Organizations', href: '/', adminOnly: false},
  {label: 'Addresses', href: '/addresses', adminOnly: false},
  {label: 'Locations', href: '/locations', adminOnly: false},
  {label: 'Coordinates', href: '/coordinates', adminOnly: false},
  {label: 'Operations', href: '/operations', adminOnly: false},
  {label: 'History', href: '/import-history', adminOnly: false},
  {label: 'Admin Panel', href: '/admin-panel', adminOnly: true}
];

const visibleMenuItems = computed(() =>
  menuItems.filter(item => !item.adminOnly || auth.user?.role === 'ADMIN')
)

watch(() => auth.user, (newUser) => {
  userLogin.value = newUser?.login || 'Guest'
})
</script>


<template>
  <header class="app-header">
    <div class="left header-block">
      <div class="group">P3306</div>
      <div class="name">
        <p>Medvedev Vladislav</p>
        <p>Aleksandrovich</p>
      </div>
    </div>

    <div class="center">
      <Link
        v-for="item in visibleMenuItems"
        :key="item.href"
        v-if="!item?.adminOnly || auth.user?.role === 'ADMIN'"
        :label="item.label"
        :href="item.href"
        :class="{ active: isActive(item.href) }"
      />
    </div>


    <div class="right">
      <span class="login">{{ auth.user?.login }}</span>
      <Button
        class="logout-button"
        @click="logout"
        label="Log out"
        type="button"
      />
    </div>
  </header>
</template>

<style scoped>
.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 2rem;
  border-bottom: 1px solid black;
}

.left, .center, .right {
  padding: 0 1rem;
  height: 5.25rem;
}

.left span {
  margin-right: 1rem;
}

.header-block {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.group {
  font-size: 2.5rem;
  font-weight: lighter;
}

.name {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.name p:first-child {
  font-size: 1.25rem;
  margin: 0;
}

.name p:last-child {
  font-size: 1.25rem;
  margin: 0;
  opacity: 0.8;
}

.center {
  display: flex;
  align-items: center;
  gap: 2rem;
  padding: 0 3rem;
}

.right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.active{
  color: rgba(79, 70, 229, 0.8);
  padding: 0.3rem 0.6rem;
  border-radius: 4px;
  text-decoration: none;
}
</style>
