<script setup>
import {ref} from "vue";
import {useRouter} from "vue-router";
import Button from "@/components/button.vue";
import Link from "@/components/link.vue";

const router = useRouter();
const userLogin = ref(localStorage.getItem("userLogin") || "Guest");

function logout() {
  localStorage.removeItem("jwt");
  localStorage.removeItem("userLogin");
  router.push("/login");
}
</script>

<template>
  <header class="app-header">
    <div class="left header-block">
      <div class="group">P3206</div>
      <div class="name">
        <p>Medvedev Vladislav</p>
        <p>Aleksandrovich</p>
      </div>
    </div>

    <div class="center">
      <Link
        class="nav-list"
        label="Home"
        href="/"
      />
      <Link
        class="nav-list"
        label="Operations"
        href="/operations"
      />
    </div>

    <div class="right">
      <span class="login">{{ userLogin }}</span>
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
  padding: 0.5rem 4rem;
  border: 1px solid black;
}

.left, .center, .right {
  padding: 0 2rem;
/*  border-radius: 0.5rem;
  border: 1px solid black;*/
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
  gap: 5rem;
  padding: 0 3rem;
}

.right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

</style>
