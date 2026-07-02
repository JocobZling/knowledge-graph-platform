<template>
  <div class="app-layout">
    <aside class="app-sidebar">
      <RouterLink class="app-brand" to="/">
        <span class="brand-mark">KG</span>
        <span>
          <strong>个人知识图谱</strong>
          <small>Knowledge OS</small>
        </span>
      </RouterLink>
      <nav class="app-nav">
        <RouterLink v-for="item in navItems" :key="item.path" class="app-nav-item" :to="item.path">
          {{ item.label }}
        </RouterLink>
      </nav>
    </aside>

    <div class="app-body">
      <main class="app-main">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup>
import { RouterLink } from 'vue-router'

const navItems = [
  { path: '/', label: 'Dashboard' },
  { path: '/daily', label: '情报中心' },
  { path: '/knowledge', label: '知识库' },
  { path: '/issues', label: '问题库' },
  { path: '/projects', label: '项目中心' },
  { path: '/prompts', label: 'Prompt 管理' },
  { path: '/graph', label: '知识图谱' },
  { path: '/timeline', label: '时间线' },
  { path: '/search', label: '全局搜索' }
]
</script>
