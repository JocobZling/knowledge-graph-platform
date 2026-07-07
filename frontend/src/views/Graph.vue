<template>
  <section class="page page-stack graph-workspace-page">
    <PageHeader title="知识图谱" eyebrow="Graph" description="以关系图方式浏览知识、问题、项目和 Prompt 的连接。">
      <template #actions>
        <el-button :loading="loading" @click="load">刷新</el-button>
      </template>
    </PageHeader>

    <BaseCard class="graph-shell" v-loading="loading">
      <EmptyState v-if="!loading && empty" title="暂无图谱数据" description="建立知识关系后，图谱会显示在这里。" />
      <div v-show="!empty" ref="chartRef" class="graph"></div>
    </BaseCard>
  </section>
</template>

<script setup>
import { nextTick, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import BaseCard from '../components/BaseCard.vue'
import EmptyState from '../components/EmptyState.vue'
import PageHeader from '../components/PageHeader.vue'
import http from '../api/http'

const chartRef = ref()
const loading = ref(false)
const empty = ref(false)
let chart

const graphPalette = {
  knowledge: '#22d3ee',
  issue: '#fb7185',
  project: '#a78bfa',
  prompt: '#34d399'
}

async function load() {
  loading.value = true
  try {
    const data = await http.get('/graph/all')
    empty.value = !data.nodes?.length
    await nextTick()
    if (empty.value) return
    chart ||= echarts.init(chartRef.value, 'dark')
    chart.setOption({
      backgroundColor: 'transparent',
      color: Object.values(graphPalette),
      tooltip: {
        backgroundColor: 'rgba(6, 7, 19, 0.92)',
        borderColor: 'rgba(255, 255, 255, 0.14)',
        textStyle: { color: '#f8fafc' }
      },
      legend: [{
        top: 4,
        data: ['knowledge', 'issue', 'project', 'prompt'],
        textStyle: { color: '#94a3b8' },
        itemGap: 18
      }],
      series: [{
        type: 'graph',
        layout: 'force',
        roam: true,
        top: 48,
        bottom: 18,
        label: {
          show: true,
          color: '#f8fafc',
          fontWeight: 700,
          formatter: '{b}'
        },
        edgeLabel: {
          show: true,
          color: '#94a3b8',
          fontSize: 11,
          formatter: (p) => p.data.name
        },
        lineStyle: {
          color: 'rgba(148, 163, 184, 0.34)',
          width: 1.2,
          curveness: 0.18
        },
        emphasis: {
          focus: 'adjacency',
          lineStyle: { color: '#22d3ee', width: 2.2 }
        },
        force: { repulsion: 300, edgeLength: 145, gravity: 0.05 },
        categories: ['knowledge', 'issue', 'project', 'prompt'].map((name) => ({ name, itemStyle: { color: graphPalette[name] } })),
        data: data.nodes.map((node) => ({
          ...node,
          category: node.type,
          symbolSize: node.type === 'project' ? 68 : 52,
          itemStyle: {
            color: graphPalette[node.type] || '#22d3ee',
            borderColor: 'rgba(255, 255, 255, 0.72)',
            borderWidth: 1,
            shadowBlur: 22,
            shadowColor: graphPalette[node.type] || '#22d3ee'
          }
        })),
        links: data.links
      }]
    })
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>
