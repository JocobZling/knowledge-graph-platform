<template>
  <section class="page page-stack">
    <PageHeader title="本地日报" eyebrow="Local Markdown" description="读取本地 Markdown 文件，不依赖后端和数据库。">
      <template #actions>
        <el-button :loading="loading" @click="loadReports">刷新</el-button>
      </template>
    </PageHeader>

    <BaseCard>
      <div class="toolbar report-toolbar">
        <el-input v-model="keyword" placeholder="搜索标题、标签或正文" clearable />
        <el-date-picker v-model="selectedDate" type="date" value-format="YYYY-MM-DD" placeholder="按日期筛选" clearable />
      </div>
    </BaseCard>

    <div class="reports-layout">
      <BaseCard title="日报列表" description="每天一个 Markdown 文件，按日期倒序显示。" v-loading="loading">
        <EmptyState v-if="!loading && !filteredReports.length" title="暂无匹配日报" description="添加 Markdown 文件后会显示在这里。" />
        <div v-else class="report-list">
          <button
            v-for="report in filteredReports"
            :key="report.file"
            class="report-item"
            :class="{ 'report-item--active': activeReport?.file === report.file }"
            type="button"
            @click="selectReport(report)"
          >
            <span>{{ report.title }}</span>
            <small>{{ report.date }}</small>
            <span class="report-tags">
              <StatusTag v-for="tag in report.tags || []" :key="tag" :value="tag" />
            </span>
          </button>
        </div>
      </BaseCard>

      <BaseCard v-loading="contentLoading">
        <EmptyState v-if="!activeReport" title="选择一篇日报" description="从左侧列表选择一篇 Markdown 日报查看内容。" />
        <article v-else class="markdown-preview">
          <header>
            <p>{{ activeReport.date }}</p>
            <h1>{{ activeReport.title }}</h1>
          </header>
          <div v-html="renderedMarkdown"></div>
        </article>
      </BaseCard>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import BaseCard from '../components/BaseCard.vue'
import EmptyState from '../components/EmptyState.vue'
import PageHeader from '../components/PageHeader.vue'
import StatusTag from '../components/StatusTag.vue'

const reports = ref([])
const reportContent = ref('')
const activeReport = ref(null)
const keyword = ref('')
const selectedDate = ref('')
const loading = ref(false)
const contentLoading = ref(false)

const filteredReports = computed(() => {
  const query = keyword.value.trim().toLowerCase()
  return reports.value.filter((report) => {
    const matchDate = !selectedDate.value || report.date === selectedDate.value
    const haystack = [report.title, report.date, ...(report.tags || []), report.content || ''].join(' ').toLowerCase()
    return matchDate && (!query || haystack.includes(query))
  })
})

const renderedMarkdown = computed(() => renderMarkdown(reportContent.value))

async function loadReports() {
  loading.value = true
  try {
    const response = await fetch('/daily-reports/index.json', { cache: 'no-cache' })
    if (!response.ok) throw new Error('未找到日报目录')
    const list = await response.json()
    reports.value = await Promise.all(
      list
        .slice()
        .sort((a, b) => String(b.date).localeCompare(String(a.date)))
        .map(async (report) => {
          const content = await fetchMarkdown(report.file)
          return { ...report, content }
        })
    )
    if (!activeReport.value && reports.value.length) selectReport(reports.value[0])
  } catch (error) {
    ElMessage.error(error.message || '日报加载失败')
  } finally {
    loading.value = false
  }
}

async function fetchMarkdown(file) {
  const response = await fetch('/daily-reports/' + file, { cache: 'no-cache' })
  if (!response.ok) return ''
  return response.text()
}

async function selectReport(report) {
  activeReport.value = report
  contentLoading.value = true
  try {
    reportContent.value = report.content || await fetchMarkdown(report.file)
  } finally {
    contentLoading.value = false
  }
}

function escapeHtml(value) {
  return String(value)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;')
}

function inlineMarkdown(value) {
  return escapeHtml(value)
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/`(.+?)`/g, '<code>$1</code>')
}

function renderMarkdown(markdown) {
  const lines = markdown.split('\n')
  const html = []
  let inList = false
  let inCode = false
  let codeLines = []

  const closeList = () => {
    if (inList) {
      html.push('</ul>')
      inList = false
    }
  }

  lines.forEach((line) => {
    if (line.startsWith('```')) {
      if (inCode) {
        html.push('<pre><code>' + escapeHtml(codeLines.join('\n')) + '</code></pre>')
        codeLines = []
      } else {
        closeList()
      }
      inCode = !inCode
      return
    }

    if (inCode) {
      codeLines.push(line)
      return
    }

    if (!line.trim()) {
      closeList()
      return
    }

    if (line.startsWith('# ')) {
      closeList()
      html.push('<h1>' + inlineMarkdown(line.slice(2)) + '</h1>')
      return
    }

    if (line.startsWith('## ')) {
      closeList()
      html.push('<h2>' + inlineMarkdown(line.slice(3)) + '</h2>')
      return
    }

    if (line.startsWith('### ')) {
      closeList()
      html.push('<h3>' + inlineMarkdown(line.slice(4)) + '</h3>')
      return
    }

    if (line.startsWith('> ')) {
      closeList()
      html.push('<blockquote>' + inlineMarkdown(line.slice(2)) + '</blockquote>')
      return
    }

    if (line.startsWith('- ')) {
      if (!inList) {
        html.push('<ul>')
        inList = true
      }
      html.push('<li>' + inlineMarkdown(line.slice(2)) + '</li>')
      return
    }

    closeList()
    html.push('<p>' + inlineMarkdown(line) + '</p>')
  })

  closeList()
  return html.join('')
}

watch(filteredReports, (value) => {
  if (activeReport.value && !value.some((report) => report.file === activeReport.value.file)) {
    activeReport.value = value[0] || null
    reportContent.value = activeReport.value?.content || ''
  }
})

onMounted(loadReports)
</script>
