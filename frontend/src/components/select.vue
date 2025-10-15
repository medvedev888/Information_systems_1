<script setup>
import {computed} from 'vue'

const props = defineProps({
  modelValue: null,
  options: {type: Array, default: () => []},
  valueKey: {type: String, default: 'id'},
  labelKey: {type: String, default: 'name'},
  allowNull: {type: Boolean, default: true},
  placeholder: {type: String, default: ' Select '}
})

const emit = defineEmits(['update:modelValue'])

const normalized = computed(() => {
  return props.options.map((opt, idx) => {
    if (opt === null) {
      return {raw: null, value: '', label: '', key: `null-${idx}`}
    }
    if (typeof opt !== 'object') {
      return {raw: opt, value: String(opt), label: String(opt), key: `prim-${idx}`}
    }
    const rawValue = opt[props.valueKey] ?? ''
    return {raw: opt, value: String(rawValue), label: null, key: `obj-${idx}`}
  })
})

const currentValue = computed(() => {
  if (props.modelValue === null) return ''
  if (typeof props.modelValue === 'object') return String(props.modelValue[props.valueKey] ?? '')
  return String(props.modelValue)
})

// красивый лейбл для объектов (координаты, адреса и т.д.)
function formatLabel(opt) {
  if (opt === null) return props.placeholder
  if (typeof opt !== 'object') return String(opt)

  if ('x' in opt && 'y' in opt && !('street' in opt)) {
    return `Coordinates ${opt.id ?? ''} | x: ${opt.x}, y: ${opt.y}`
  }

  if ('street' in opt && 'town' in opt) {
    const t = opt.town
    let townStr = ''
    if (t && typeof t === 'object' && 'x' in t && 'y' in t) {
      townStr = `, town { x: ${t.x}, y: ${t.y}${'z' in t ? ', z: ' + t.z : ''} }`
    }
    return `Address ${opt.id ?? ''} | street: ${opt.street}${townStr}`
  }

  return String(opt[props.labelKey] ?? JSON.stringify(opt))
}

function onChange(e) {
  const val = e.target.value
  if (val === '') {
    emit('update:modelValue', null)
    return
  }

  const found = normalized.value.find(o => o.value === val)
  if (!found) {
    emit('update:modelValue', val)
    return
  }

  emit('update:modelValue', found.raw)
}
</script>

<template>
  <select :value="currentValue" @change="onChange" class="select">
    <option v-if="allowNull" value="">{{ placeholder }}</option>

    <option
      v-for="opt in normalized"
      :key="opt.key"
      :value="opt.value"
    >
      {{
        opt.raw && typeof opt.raw === 'object' ? formatLabel(opt.raw) : (opt.label ?? formatLabel(opt.raw))
      }}
    </option>
  </select>
</template>

<style scoped>
.select {
  padding: 0.5rem;
  border-radius: 4px;
  border: 1px solid #ccc;
  width: 100%;
  box-sizing: border-box;
  height: 2rem;
}

.select:focus {
  border-color: black;
  outline: none;
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.3);
}
</style>
