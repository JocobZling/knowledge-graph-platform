<template>
  <section class="page page-stack">
    <PageHeader title="情报中心" eyebrow="Daily Brief" description="按主题归档每天由 ChatGPT 生成的科技日报、AI 动态和测试开发情报。">
      <template #actions>
        <el-button :loading="loading" @click="loadBriefs">刷新</el-button>
      </template>
    </PageHeader>

    <BaseCard>
      <div class="daily-filter">
        <el-input v-model="filters.keyword" placeholder="搜索标题、摘要、标签或正文" clearable @keyup.enter="loadBriefs" />
        <el-select v-model="filters.topic" placeholder="主题" clearable>
          <el-option v-for="item in topicOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-select v-model="filters.tag" placeholder="标签" clearable>
          <el-option v-for="item in tagOptions" :key="item" :label="item" :value="item" />
        </el-select>
        <el-date-picker v-model="filters.dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始日期" end-placeholder="结束日期" />
      </div>
    </BaseCard>

    <BaseCard v-loading="loading">
      <EmptyState v-if="!loading && !filteredBriefs.length" title="暂无匹配情报" description="同步 Markdown 或调整筛选条件后再试试。" />
      <div v-else class="daily-card-list">
        <DailyBriefCard v-for="brief in filteredBriefs" :key="brief.id" :brief="brief" />
      </div>
    </BaseCard>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import BaseCard from '../components/BaseCard.vue'
import DailyBriefCard from '../components/DailyBriefCard.vue'
import EmptyState from '../components/EmptyState.vue'
import PageHeader from '../components/PageHeader.vue'
import http from '../api/http'

const briefs = ref([])
const loading = ref(false)
const usingLocalFallback = ref(false)
const filters = reactive({
  keyword: '',
  topic: '',
  tag: '',
  dateRange: []
})

const filteredBriefs = computed(() => {
  const keyword = filters.keyword.trim().toLowerCase()
  const [startDate, endDate] = filters.dateRange || []
  return briefs.value.filter((brief) => {
    const date = brief.briefDate || brief.date
    const tags = normalizeTags(brief.tags)
    const topics = unique([brief.type, brief.category].filter(Boolean))
    const haystack = [brief.title, brief.summary, brief.content, ...topics, ...tags].join(' ').toLowerCase()
    return (!keyword || haystack.includes(keyword))
      && (!filters.topic || topics.some((topic) => normalizeOptionKey(topic) === filters.topic))
      && (!filters.tag || tags.includes(filters.tag))
      && (!startDate || date >= startDate)
      && (!endDate || date <= endDate)
  })
})

const topicOptions = computed(() => uniqueOptions(briefs.value.flatMap((brief) => [brief.type, brief.category].filter(Boolean))))
const tagOptions = computed(() => unique(briefs.value.flatMap((brief) => normalizeTags(brief.tags))))

async function loadBriefs() {
  loading.value = true
  try {
    usingLocalFallback.value = false
    const [startDate, endDate] = filters.dateRange || []
    briefs.value = await http.get('/daily-brief/list', {
      params: {
        keyword: filters.keyword || undefined,
        tag: filters.tag || undefined,
        startDate,
        endDate
      }
    })
  } catch {
    usingLocalFallback.value = true
    briefs.value = await loadLocalBriefs()
    ElMessage.info('后端未连接，已切换为本地 Markdown 预览')
  } finally {
    loading.value = false
  }
}

async function loadLocalBriefs() {
  const response = await fetch('/daily-reports/index.json', { cache: 'no-cache' })
  if (!response.ok) return []
  const list = await response.json()
  const items = await Promise.all(list.map(async (item, index) => {
    const content = await fetchMarkdown(item.file)
    return { ...item, id: item.id || encodeURIComponent(item.file || String(index)), briefDate: item.date, content }
  }))
  return items.sort((a, b) => String(b.briefDate || b.date).localeCompare(String(a.briefDate || a.date)))
}

async function fetchMarkdown(file) {
  const response = await fetch('/daily-reports/' + file, { cache: 'no-cache' })
  return response.ok ? response.text() : ''
}

function normalizeTags(tags) {
  if (Array.isArray(tags)) return tags
  if (!tags) return []
  try {
    return JSON.parse(tags)
  } catch {
    return String(tags).split(',').map((tag) => tag.trim()).filter(Boolean)
  }
}

function unique(values) {
  return [...new Set(values)]
}

function uniqueOptions(values) {
  const options = new Map()
  values.forEach((value) => {
    const label = String(value || '').trim()
    if (!label) return
    const key = normalizeOptionKey(label)
    const current = options.get(key)
    if (!current || scoreOptionLabel(label) > scoreOptionLabel(current.label)) {
      options.set(key, { label: formatOptionLabel(label), value: key })
    }
  })
  return [...options.values()]
}

function normalizeOptionKey(value) {
  return String(value || '').trim().toLowerCase().replace(/[\s_-]+/g, '')
}

function formatOptionLabel(value) {
  const text = String(value || '').trim()
  if (!text) return ''
  if (!/[A-Z]/.test(text)) {
    return text.split(/[\s_-]+/).filter(Boolean).map((word) => word.toUpperCase() === 'AI' ? 'AI' : word.charAt(0).toUpperCase() + word.slice(1)).join(' ')
  }
  return text.replace(/[-_]+/g, ' ')
}

function scoreOptionLabel(value) {
  const text = String(value || '')
  return (/[A-Z]/.test(text) ? 2 : 0) + (/[-_]/.test(text) ? 0 : 1)
}

onMounted(loadBriefs)
</script>
