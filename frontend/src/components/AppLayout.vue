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
          <span class="app-nav-item__icon">{{ item.icon }}</span>
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
  { path: '/', label: 'Dashboard', icon: '01' },
  { path: '/daily', label: '情报中心', icon: '02' },
  { path: '/knowledge', label: '知识库', icon: '03' },
  { path: '/issues', label: '问题库', icon: '04' },
  { path: '/projects', label: '项目中心', icon: '05' },
  { path: '/prompts', label: 'Prompt 管理', icon: '06' },
  { path: '/graph', label: '知识图谱', icon: '07' },
  { path: '/timeline', label: '时间线', icon: '08' },
  { path: '/search', label: '全局搜索', icon: '09' }
]
</script>
