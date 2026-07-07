<template>
  <section class="workspace-home" v-loading="loading">
    <header class="workspace-topbar">
      <div>
        <p class="workspace-eyebrow">AI Workspace Home</p>
        <h1>Knowledge OS</h1>
      </div>
      <RouterLink class="command-search" to="/search" aria-label="打开全局搜索">
        <span class="command-search__pulse"></span>
        <span>Search knowledge, briefs, projects...</span>
        <kbd>Ctrl K</kbd>
      </RouterLink>
    </header>

    <section class="workspace-hero">
      <div class="hero-copy">
        <span class="status-pill">Agent online · {{ todayLabel }}</span>
        <h2>把知识、日报、项目和问题记录收束到一个智能控制中心。</h2>
        <p>
          首页重新组织为 AI OS 工作台：先看今日摘要，再进入 Daily Brief、知识图谱、项目追踪和全局搜索。
        </p>
        <div class="hero-actions">
          <RouterLink class="glow-button" to="/daily">进入情报中心</RouterLink>
          <RouterLink class="ghost-button" to="/graph">查看知识图谱</RouterLink>
        </div>
      </div>

      <div class="brain-panel" aria-label="知识图谱预览">
        <div class="brain-orbit brain-orbit--outer"></div>
        <div class="brain-orbit brain-orbit--middle"></div>
        <div class="brain-core">AI</div>
        <span v-for="node in graphNodes" :key="node.label" class="graph-node" :class="node.className">
          {{ node.label }}
        </span>
      </div>
    </section>

    <section class="workspace-grid">
      <article v-for="item in metrics" :key="item.label" class="ai-card metric-tile">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
        <small>{{ item.hint }}</small>
      </article>

      <article class="ai-card brief-card">
        <div class="card-heading">
          <span>今日摘要</span>
          <RouterLink to="/daily">AI Daily</RouterLink>
        </div>
        <h3>{{ todaySummary.title }}</h3>
        <p>{{ todaySummary.description }}</p>
        <div class="brief-tags">
          <span v-for="tag in todaySummary.tags" :key="tag">{{ tag }}</span>
        </div>
      </article>

      <RouterLink v-for="entry in workspaceEntries" :key="entry.title" class="ai-card entry-card" :to="entry.to">
        <span class="entry-card__icon">{{ entry.icon }}</span>
        <div>
          <small>{{ entry.kicker }}</small>
          <h3>{{ entry.title }}</h3>
          <p>{{ entry.description }}</p>
        </div>
      </RouterLink>

      <article class="ai-card activity-card">
        <div class="card-heading">
          <span>Agent 状态</span>
          <b>{{ agentState }}</b>
        </div>
        <div class="activity-list">
          <div v-for="item in agentActivities" :key="item.title" class="activity-item">
            <span></span>
            <div>
              <strong>{{ item.title }}</strong>
              <small>{{ item.detail }}</small>
            </div>
          </div>
        </div>
      </article>

      <article class="ai-card update-card">
        <div class="card-heading">
          <span>最近更新</span>
          <RouterLink to="/projects">Projects</RouterLink>
        </div>
        <div class="update-list">
          <div v-for="item in recentWork" :key="item.title" class="update-item">
            <strong>{{ item.title }}</strong>
            <span>{{ item.meta }}</span>
          </div>
        </div>
      </article>
    </section>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import http from '../api/http'

const data = ref({})
const loading = ref(false)

const metrics = computed(() => [
  { label: '知识节点', value: data.value.knowledgeCount || 0, hint: '可沉淀的长期资产' },
  { label: '问题记录', value: data.value.issueCount || 0, hint: '待复盘与追踪' },
  { label: '项目数量', value: data.value.projectCount || 0, hint: '正在连接上下文' },
  { label: 'Prompt', value: data.value.promptCount || 0, hint: '可复用工作流' }
])
const recentIssues = computed(() => data.value.recentIssues || [])
const activeProjects = computed(() => data.value.activeProjects || [])
const todayLabel = new Intl.DateTimeFormat('zh-CN', { month: 'short', day: 'numeric', weekday: 'short' }).format(new Date())
const todaySummary = {
  title: '今日优先级：整理知识入口，保持日报、图谱与项目上下文连续。',
  description: '后端未启动时也保留完整首页视觉；接口返回后会自动替换统计、最近问题和项目动态。',
  tags: ['Daily Brief', 'Knowledge Graph', 'Project Pulse']
}
const graphNodes = [
  { label: 'Brief', className: 'graph-node--brief' },
  { label: 'Graph', className: 'graph-node--graph' },
  { label: 'Prompt', className: 'graph-node--prompt' },
  { label: 'Issue', className: 'graph-node--issue' }
]
const workspaceEntries = [
  {
    icon: 'DB',
    kicker: 'AI Daily',
    title: '日报情报入口',
    description: '用科技 newsletter 的方式阅读本地 Markdown 与数据库同步内容。',
    to: '/daily'
  },
  {
    icon: 'KG',
    kicker: 'Neural Graph',
    title: '知识图谱预览',
    description: '从节点和关系进入长期知识网络，快速看见上下文连接。',
    to: '/graph'
  },
  {
    icon: 'QA',
    kicker: 'Testing Signal',
    title: '智能测试沉淀',
    description: '把问题记录、复盘原因和项目状态汇总为可追踪资产。',
    to: '/issues'
  },
  {
    icon: 'GH',
    kicker: 'GitHub Pulse',
    title: '最近更新卡片',
    description: '预留 GitHub 自动化状态入口，后续接入提交与 PR 动态。',
    to: '/projects'
  }
]
const agentActivities = [
  { title: 'Context Sync', detail: '知识库、项目与问题状态已汇总到首页。' },
  { title: 'Daily Brief Ready', detail: '日报入口保持本地 fallback 预览能力。' },
  { title: 'Graph Preview', detail: '图谱入口突出节点关系和认知网络。' }
]
const agentState = computed(() => loading.value ? 'Syncing' : 'Ready')
const recentWork = computed(() => {
  const issueItems = recentIssues.value.slice(0, 2).map((item) => ({
    title: item.title,
    meta: `${item.status || 'open'} · ${item.problemType || 'issue'}`
  }))
  const projectItems = activeProjects.value.slice(0, 2).map((item) => ({
    title: item.name,
    meta: `${item.status || 'active'} · ${item.priority || 'normal'}`
  }))
  const items = [...issueItems, ...projectItems]
  return items.length ? items : [
    { title: 'Daily Brief 视觉入口', meta: 'AI Daily · ready' },
    { title: '知识图谱工作台', meta: 'Graph · preview' },
    { title: '项目中心脉冲', meta: 'Projects · standby' }
  ]
})

async function load() {
  loading.value = true
  try {
    data.value = await http.get('/dashboard/summary')
  } catch (error) {
    data.value = {}
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>
