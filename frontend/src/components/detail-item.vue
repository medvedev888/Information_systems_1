<script setup>

defineProps({
  label: String,
  value: [Object, String, Number, Boolean, null]
});

</script>

<template>
  <li>
    <strong>{{ label }}: </strong>
    <span v-if="value && typeof value === 'object'">
      <ul>
        <DetailItem v-for="(v,k) in value" :key="k" :label="k" :value="v"/>
      </ul>
    </span>
    <span v-else>{{ value }}</span>
  </li>
</template>
