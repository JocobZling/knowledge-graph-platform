<template>
  <RouterLink class="daily-card" :to="'/daily/' + brief.id">
    <div class="daily-card__meta">
      <span>{{ brief.briefDate || brief.date }}</span>
      <StatusTag :value="brief.status || 'published'" />
    </div>
    <h3>{{ brief.title }}</h3>
    <p>{{ brief.summary || '暂无摘要' }}</p>
    <div class="daily-card__footer">
      <span>{{ brief.category || brief.type || 'daily' }}</span>
      <span class="report-tags">
        <StatusTag v-for="tag in normalizedTags" :key="tag" :value="tag" />
      </span>
    </div>
  </RouterLink>
</template>

<script setup>
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import StatusTag from './StatusTag.vue'

const props = defineProps({
  brief: { type: Object, required: true }
})

const normalizedTags = computed(() => {
  if (Array.isArray(props.brief.tags)) return props.brief.tags
  if (!props.brief.tags) return []
  try {
    return JSON.parse(props.brief.tags)
  } catch {
    return String(props.brief.tags).split(',').map((tag) => tag.trim()).filter(Boolean)
  }
})
</script>
