<template>
  <section class="page page-stack">
    <PageHeader :title="brief?.title || '情报详情'" eyebrow="Intelligence Detail" :description="brief?.summary || '阅读 Markdown 情报详情。'">
      <template #actions>
        <el-button text @click="$router.push('/daily')">返回列表</el-button>
      </template>
    </PageHeader>

    <BaseCard v-loading="loading">
      <EmptyState v-if="!loading && !brief" title="情报不存在" description="这篇情报可能尚未同步或已归档。" />
      <article v-else-if="brief" class="daily-detail">
        <div class="daily-detail__meta">
          <span>{{ brief.briefDate || brief.date }}</span>
          <span>{{ brief.type }}</span>
          <span>{{ brief.category }}</span>
          <span>{{ brief.source }}</span>
          <StatusTag :value="brief.status" />
        </div>
        <div class="report-tags daily-detail__tags">
          <StatusTag v-for="tag in tags" :key="tag" :value="tag" />
        </div>
        <MarkdownViewer :content="brief.content" />
      </article>
    </BaseCard>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import BaseCard from '../components/BaseCard.vue'
import EmptyState from '../components/EmptyState.vue'
import MarkdownViewer from '../components/MarkdownViewer.vue'
import PageHeader from '../components/PageHeader.vue'
import StatusTag from '../components/StatusTag.vue'
import http from '../api/http'

const route = useRoute()
const brief = ref(null)
const loading = ref(false)
const tags = computed(() => normalizeTags(brief.value?.tags))

async function loadBrief() {
  loading.value = true
  try {
    brief.value = await http.get('/daily-brief/' + route.params.id)
  } catch {
    brief.value = await loadLocalBrief()
  } finally {
    loading.value = false
  }
}

async function loadLocalBrief() {
  const response = await fetch('/daily-reports/index.json', { cache: 'no-cache' })
  if (!response.ok) return null
  const list = await response.json()
  const id = decodeURIComponent(String(route.params.id))
  const item = list.find((brief) => String(brief.id) === id || brief.file === id || encodeURIComponent(brief.file) === route.params.id)
  if (!item) return null
  const contentResponse = await fetch('/daily-reports/' + item.file, { cache: 'no-cache' })
  return { ...item, id: item.id || encodeURIComponent(item.file), briefDate: item.date, content: contentResponse.ok ? await contentResponse.text() : '' }
}

function normalizeTags(tagsValue) {
  if (Array.isArray(tagsValue)) return tagsValue
  if (!tagsValue) return []
  try {
    return JSON.parse(tagsValue)
  } catch {
    return String(tagsValue).split(',').map((tag) => tag.trim()).filter(Boolean)
  }
}

onMounted(loadBrief)
</script>