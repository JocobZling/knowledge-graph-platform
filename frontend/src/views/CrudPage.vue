<template>
  <section class="page page-stack data-workspace-page">
    <PageHeader :title="config.title" :eyebrow="config.eyebrow" :description="config.description">
      <template #actions>
        <el-button type="primary" @click="openCreate">新增</el-button>
      </template>
    </PageHeader>

    <BaseCard class="command-panel">
      <div class="toolbar command-toolbar">
        <el-input v-model="keyword" placeholder="搜索关键词" clearable @keyup.enter="load" />
        <el-button @click="load">搜索</el-button>
      </div>
    </BaseCard>

    <BaseCard class="records-panel" v-loading="loading">
      <EmptyState v-if="!loading && !rows.length" :title="emptyTitle" description="新增内容后会在这里展示。" />
      <div v-else class="record-grid">
        <article v-for="row in rows" :key="row.id || row[config.main]" class="record-card">
          <div class="record-card__main">
            <span class="record-card__eyebrow">{{ config.eyebrow }}</span>
            <h3>{{ row[config.main] || '未命名记录' }}</h3>
          </div>
          <div v-if="recordPreview(row).length" class="record-card__preview">
            <div v-for="item in recordPreview(row)" :key="item.label" class="record-preview-item">
              <span>{{ item.label }}</span>
              <p>{{ item.text }}</p>
            </div>
          </div>
          <div class="record-card__meta">
            <div v-for="col in config.columns" :key="col.prop" class="record-meta-item">
              <span>{{ col.label }}</span>
              <StatusTag v-if="col.tag" :value="row[col.prop]" />
              <strong v-else>{{ row[col.prop] || '-' }}</strong>
            </div>
          </div>
          <div class="record-card__actions">
            <el-button size="small" text @click="openEdit(row)">编辑</el-button>
            <el-button v-if="type === 'issues'" size="small" text type="primary" @click="solve(row)">解决</el-button>
            <el-button size="small" text type="danger" @click="remove(row)">删除</el-button>
          </div>
        </article>
      </div>
    </BaseCard>

    <el-dialog v-model="visible" :title="form.id ? '编辑' : '新增'" width="720px">
      <el-form class="form-stack" label-width="110px">
        <el-form-item v-for="field in config.fields" :key="field.prop" :label="field.label">
          <el-input v-if="field.type !== 'textarea'" v-model="form[field.prop]" />
          <el-input v-else v-model="form[field.prop]" type="textarea" :rows="5" />
        </el-form-item>
        <el-form-item label="标签 JSON">
          <el-input v-model="form.tags" placeholder='["SpringBoot","Bug"]' />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import BaseCard from '../components/BaseCard.vue'
import EmptyState from '../components/EmptyState.vue'
import PageHeader from '../components/PageHeader.vue'
import StatusTag from '../components/StatusTag.vue'
import http from '../api/http'

const props = defineProps({ type: String })
const rows = ref([])
const keyword = ref('')
const visible = ref(false)
const loading = ref(false)
const saving = ref(false)
const form = reactive({})

const configs = {
  knowledge: {
    title: '知识库', eyebrow: 'Knowledge', description: '沉淀可复用的概念、经验和资料。', api: '/knowledge', main: 'title',
    previewFields: [
      { prop: 'summary', label: '摘要' },
      { prop: 'content', label: '正文' },
      { prop: 'source', label: '来源' }
    ],
    columns: [{ prop: 'category', label: '分类', width: 140 }, { prop: 'status', label: '状态', width: 110, tag: true }, { prop: 'level', label: '熟悉度', width: 100, tag: true }],
    fields: [{ prop: 'title', label: '标题' }, { prop: 'summary', label: '摘要', type: 'textarea' }, { prop: 'content', label: '正文', type: 'textarea' }, { prop: 'category', label: '分类' }, { prop: 'status', label: '状态' }, { prop: 'source', label: '来源' }]
  },
  issues: {
    title: '问题库', eyebrow: 'Issues', description: '记录问题背景、原因、解决方案和复盘信息。', api: '/issues', main: 'title',
    previewFields: [
      { prop: 'errorMessage', label: '报错' },
      { prop: 'background', label: '背景' },
      { prop: 'reason', label: '原因' },
      { prop: 'solution', label: '方案' }
    ],
    columns: [{ prop: 'problemType', label: '类型', width: 150 }, { prop: 'status', label: '状态', width: 110, tag: true }, { prop: 'difficulty', label: '难度', width: 100, tag: true }],
    fields: [{ prop: 'title', label: '标题' }, { prop: 'problemType', label: '问题类型' }, { prop: 'errorMessage', label: '报错信息', type: 'textarea' }, { prop: 'background', label: '背景', type: 'textarea' }, { prop: 'reason', label: '原因', type: 'textarea' }, { prop: 'solution', label: '解决方案', type: 'textarea' }]
  },
  projects: {
    title: '项目中心', eyebrow: 'Projects', description: '管理项目进展、优先级和关键上下文。', api: '/projects', main: 'name',
    previewFields: [
      { prop: 'summary', label: '摘要' },
      { prop: 'description', label: '描述' }
    ],
    columns: [{ prop: 'status', label: '状态', width: 120, tag: true }, { prop: 'priority', label: '优先级', width: 100, tag: true }],
    fields: [{ prop: 'name', label: '名称' }, { prop: 'summary', label: '摘要', type: 'textarea' }, { prop: 'description', label: '描述', type: 'textarea' }, { prop: 'status', label: '状态' }]
  },
  prompts: {
    title: 'Prompt 管理', eyebrow: 'Prompts', description: '维护不同场景下可复用的 Prompt 模板。', api: '/prompts', main: 'title',
    previewFields: [
      { prop: 'scenario', label: '场景' },
      { prop: 'content', label: '内容' },
      { prop: 'model', label: '模型' }
    ],
    columns: [{ prop: 'scenario', label: '场景', width: 180 }, { prop: 'model', label: '模型', width: 120 }],
    fields: [{ prop: 'title', label: '标题' }, { prop: 'scenario', label: '场景' }, { prop: 'content', label: '内容', type: 'textarea' }, { prop: 'model', label: '模型' }]
  }
}
const type = computed(() => props.type)
const config = computed(() => configs[props.type])
const emptyTitle = computed(() => '暂无' + config.value.title + '记录')

async function load() {
  loading.value = true
  try {
    rows.value = await http.get(config.value.api + '/list', { params: { keyword: keyword.value } })
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}

function resetForm(row = {}) {
  Object.keys(form).forEach((key) => delete form[key])
  Object.assign(form, { tags: '[]' }, row)
}

function openCreate() {
  resetForm()
  visible.value = true
}

function openEdit(row) {
  resetForm(row)
  visible.value = true
}

function previewText(value) {
  return String(value || '').replace(/\s+/g, ' ').trim().slice(0, 120)
}

function recordPreview(row) {
  return config.value.previewFields
    .map((field) => ({ label: field.label, text: previewText(row[field.prop]) }))
    .filter((item) => item.text)
    .slice(0, 3)
}

async function save() {
  saving.value = true
  try {
    await http[form.id ? 'put' : 'post'](config.value.api + '/' + (form.id ? 'update' : 'create'), form)
    visible.value = false
    ElMessage.success('已保存')
    load()
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    saving.value = false
  }
}

async function solve(row) {
  try {
    await http.put('/issues/' + row.id + '/solve')
    ElMessage.success('已标记解决')
    load()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

async function remove(row) {
  try {
    await ElMessageBox.confirm('确认删除这条记录？删除后不可恢复。', '删除确认', { type: 'warning' })
    await http.delete(config.value.api + '/' + row.id)
    ElMessage.success('已删除')
    load()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.message || '删除已取消')
  }
}

watch(() => props.type, load)
onMounted(load)
</script>
