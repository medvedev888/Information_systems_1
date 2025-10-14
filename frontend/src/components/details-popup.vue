<script setup>
import DetailItem from '@/components/detail-item.vue'

defineProps({
  data: Object,
  x: Number,
  y: Number,
  visible: Boolean
});
</script>

<template>
  <div v-if="visible" class="details-popup" :style="{ left: x+'px', top:y+'px' }">
    <ul class="detail-list">
      <DetailItem v-for="(v,k) in data" :key="k" :label="k" :value="v"/>
    </ul>
  </div>
</template>

<style scoped>
.details-popup {
  position: absolute;
  background: white;
  padding: 0.5rem 1rem;
  border: 1px solid black;
  border-radius: 0.5rem;
  font-size: 0.9rem;
  box-shadow: 0 2px 6px rgba(0,0,0,0.15);
  z-index: 1000;
  pointer-events: none;
}

.detail-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.detail-list ul {
  margin-left: 1rem;
}

</style>
