<template>
  <RouterLink class="daily-card" :to="'/daily/' + brief.id">
    <div class="daily-card__meta">
      <span>{{ brief.briefDate || brief.date }}</span>
      <StatusTag :value="brief.status || 'published'" />
    </div>
    <p>{{ brief.summary || '暂无摘要' }}</p>
    <div class="daily-card__footer">
      <span class="report-tags">
        <StatusTag v-for="tag in displayTags" :key="tag" :value="tag" />
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

const displayTags = computed(() => uniqueTags([
  props.brief.category,
  props.brief.type,
  ...normalizeTags(props.brief.tags)
]))

function normalizeTags(tags) {
  if (Array.isArray(tags)) return tags
  if (!tags) return []
  try {
    return JSON.parse(tags)
  } catch {
    return String(tags).split(',').map((tag) => tag.trim()).filter(Boolean)
  }
}

function uniqueTags(tags) {
  const seen = new Set()
  return tags.filter((tag) => {
    const text = String(tag || '').trim()
    if (!text) return false
    const key = text.toLowerCase().replace(/[\s_-]+/g, '')
    if (seen.has(key)) return false
    seen.add(key)
    return true
  })
}
</script>
