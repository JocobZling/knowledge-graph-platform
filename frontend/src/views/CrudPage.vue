<template>
  <section class="page">
    <div class="page-title">
      <h1>{{ config.title }}</h1>
      <el-button type="primary" @click="openCreate">新增</el-button>
    </div>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索关键词" clearable @keyup.enter="load" />
      <el-button @click="load">搜索</el-button>
    </div>
    <div class="panel">
      <el-table :data="rows" stripe>
        <el-table-column :prop="config.main" label="标题" min-width="220" />
        <el-table-column v-for="col in config.columns" :key="col.prop" :prop="col.prop" :label="col.label" :width="col.width" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="type === 'issues'" size="small" type="success" @click="solve(row)">解决</el-button>
            <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-dialog v-model="visible" :title="form.id ? '编辑' : '新增'" width="720px">
      <el-form label-width="110px">
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
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>
<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '../api/http'

const props = defineProps({ type: String })
const rows = ref([])
const keyword = ref('')
const visible = ref(false)
const form = reactive({})

const configs = {
  knowledge: {
    title: '知识库', api: '/knowledge', main: 'title',
    columns: [{ prop: 'category', label: '分类', width: 140 }, { prop: 'status', label: '状态', width: 110 }, { prop: 'level', label: '熟悉度', width: 100 }],
    fields: [{ prop: 'title', label: '标题' }, { prop: 'summary', label: '摘要', type: 'textarea' }, { prop: 'content', label: '正文', type: 'textarea' }, { prop: 'category', label: '分类' }, { prop: 'status', label: '状态' }, { prop: 'source', label: '来源' }]
  },
  issues: {
    title: '问题库', api: '/issues', main: 'title',
    columns: [{ prop: 'problemType', label: '类型', width: 150 }, { prop: 'status', label: '状态', width: 110 }, { prop: 'difficulty', label: '难度', width: 100 }],
    fields: [{ prop: 'title', label: '标题' }, { prop: 'problemType', label: '问题类型' }, { prop: 'errorMessage', label: '报错信息', type: 'textarea' }, { prop: 'background', label: '背景', type: 'textarea' }, { prop: 'reason', label: '原因', type: 'textarea' }, { prop: 'solution', label: '解决方案', type: 'textarea' }]
  },
  projects: {
    title: '项目中心', api: '/projects', main: 'name',
    columns: [{ prop: 'status', label: '状态', width: 120 }, { prop: 'priority', label: '优先级', width: 100 }],
    fields: [{ prop: 'name', label: '名称' }, { prop: 'summary', label: '摘要', type: 'textarea' }, { prop: 'description', label: '描述', type: 'textarea' }, { prop: 'status', label: '状态' }]
  },
  prompts: {
    title: 'Prompt 管理', api: '/prompts', main: 'title',
    columns: [{ prop: 'scenario', label: '场景', width: 180 }, { prop: 'model', label: '模型', width: 120 }],
    fields: [{ prop: 'title', label: '标题' }, { prop: 'scenario', label: '场景' }, { prop: 'content', label: '内容', type: 'textarea' }, { prop: 'model', label: '模型' }]
  }
}
const type = computed(() => props.type)
const config = computed(() => configs[props.type])

async function load() { rows.value = await http.get(`${config.value.api}/list`, { params: { keyword: keyword.value } }) }
function resetForm(row = {}) { Object.keys(form).forEach((k) => delete form[k]); Object.assign(form, { tags: '[]' }, row) }
function openCreate() { resetForm(); visible.value = true }
function openEdit(row) { resetForm(row); visible.value = true }
async function save() {
  await http[form.id ? 'put' : 'post'](`${config.value.api}/${form.id ? 'update' : 'create'}`, form)
  visible.value = false
  ElMessage.success('已保存')
  load()
}
async function solve(row) { await http.put(`/issues/${row.id}/solve`); ElMessage.success('已标记解决'); load() }
async function remove(row) {
  await ElMessageBox.confirm('确认删除这条记录？')
  await http.delete(`${config.value.api}/${row.id}`)
  ElMessage.success('已删除')
  load()
}
watch(() => props.type, load)
onMounted(load)
</script>
