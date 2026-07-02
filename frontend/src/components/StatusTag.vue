<template>
  <span class="status-tag" :class="`status-tag--${tone}`">
    {{ text || '未设置' }}
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  value: { type: [String, Number], default: '' }
})

const text = computed(() => String(props.value || '').trim())
const tone = computed(() => {
  const value = text.value.toLowerCase()
  if (['done', 'solved', 'resolved', 'active', '进行中', '已解决', '完成'].some((item) => value.includes(item))) return 'success'
  if (['high', 'urgent', 'blocked', '高', '紧急', '阻塞'].some((item) => value.includes(item))) return 'danger'
  if (['medium', 'pending', 'todo', '中', '待处理'].some((item) => value.includes(item))) return 'warning'
  return 'neutral'
})
</script>
