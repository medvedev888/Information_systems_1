<script setup>
defineProps({
  label: {type: String, default: "Go to link"},
  href: {type: String, default: "#"}
})
defineEmits(["click"])
</script>

<template>
  <a
    :href="href"
    class="link"
    @click="$emit('click', $event)"
  >
    {{ label }}
  </a>
</template>

<style scoped>
.link {
  text-decoration: underline;
  display: inline-block;
  padding: 0;
  color: black;
  border-radius: 0.375rem;
  cursor: pointer;
  font-weight: 300;
  transition: background 0.2s;
}

.link:hover {
  color: rgba(79, 70, 229, 0.8);
}
</style>
