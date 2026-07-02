<template>
  <div class="empty-state">
    <div class="empty-state__icon">∅</div>
    <h3>{{ title }}</h3>
    <p>{{ description }}</p>
    <slot />
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, default: '暂无数据' },
  description: { type: String, default: '当前没有可展示的内容。' }
})
</script>
