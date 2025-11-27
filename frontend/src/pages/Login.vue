<script setup>
import Form from "@/components/forms/form-auth.vue"
import {reactive} from "vue";
import router from "@/router/index.js";
import Link from "@/components/link.vue";
import axios from "@/axios.js";
import {useAuthStore} from "@/stores/authStore.js";


const auth = useAuthStore();
const formData = reactive({login: "", password: ""});

async function authRequest(endpoint, login, password) {
  try {
    const res = await axios.post(`/auth/${endpoint}`, { login, password });
    localStorage.setItem("jwt", res.data.token);
    return { success: true };
  } catch (err) {
    if (err.response) {
      return {
        success: false,
        errors: err.response.data.errors || { general: err.response.data.message }
      };
    } else {
      return { success: false, errors: { general: "Network error" } };
    }
  }
}

async function handleLogin(data) {
  Object.assign(formData, data);

  const result = await authRequest("login", formData.login, formData.password);

  if (result.success) {
    await auth.fetchMe();
    await router.push("/");
  } else {
    alert(Object.values(result.errors).join("\n"));
  }
}
</script>


<template>
  <div class="container">
    <h1>Login Page</h1>
    <Form
      :fields="[
        { modelKey: 'login', text: 'Enter login', type: 'text' },
        { modelKey: 'password', text: 'Enter password', type: 'password' }
      ]"
      buttonLabel="Login"
      @submit="handleLogin"
    />
    <div class="container2">
      <h4>If you don’t have an account:</h4>
      <Link
        label="Register"
        href="/registration">
      </Link>
    </div>
  </div>
</template>


<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  gap: 1rem;
}

.container2 {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 1rem;
}

h1, h4 {
  margin: 0;
  font-weight: 300;
}
</style>
