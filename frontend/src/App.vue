<template>
  <el-container class="app-shell">
    <el-aside width="232px" class="sidebar">
      <div class="brand">个人知识图谱</div>
      <el-menu router :default-active="$route.path" class="nav">
        <el-menu-item index="/">Dashboard</el-menu-item>
        <el-menu-item index="/knowledge">知识库</el-menu-item>
        <el-menu-item index="/issues">问题库</el-menu-item>
        <el-menu-item index="/projects">项目中心</el-menu-item>
        <el-menu-item index="/prompts">Prompt 管理</el-menu-item>
        <el-menu-item index="/graph">知识图谱</el-menu-item>
        <el-menu-item index="/timeline">时间轴</el-menu-item>
        <el-menu-item index="/search">全局搜索</el-menu-item>
      </el-menu>
    </el-aside>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>
