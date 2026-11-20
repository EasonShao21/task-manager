<script setup>
import { computed, onMounted, ref } from 'vue'

// [AI assisted chat-009] AI 協助將介面由本機暫存改為呼叫 Spring Boot 名片 API。
// [AI assisted chat-010] 手機經區網開啟時，API 網址沿用電腦的區網主機名稱。
const apiBase = import.meta.env.VITE_API_BASE_URL || `${globalThis.location.protocol}//${globalThis.location.hostname}:8080/api`
const cards = ref([])
const shareOrigin = ref(globalThis.location.origin)
const selectedId = ref(null)
const loading = ref(true)
const search = ref('')
const modalOpen = ref(false)
const editingId = ref(null)
const toast = ref('')
const emptyForm = () => ({ name: '', role: '', company: '', email: '', phone: '', website: '', location: '', color: 'violet', displayFields: ['email', 'phone', 'website'], published: true })
const form = ref(emptyForm())
const selected = computed(() => cards.value.find(card => card.id === selectedId.value) || cards.value[0])
const filteredCards = computed(() => cards.value.filter(card => `${card.name} ${card.role} ${card.company}`.toLowerCase().includes(search.value.toLowerCase())))
const publishedCount = computed(() => cards.value.filter(card => card.published).length)
function initials(name) { return name.trim().split(/\s+/).map(part => part[0]).slice(0, 2).join('').toUpperCase() }
async function request(path = '', options = {}) {
  const response = await fetch(`${apiBase}/cards${path}`, { headers: { 'Content-Type': 'application/json' }, ...options })
  if (!response.ok) throw new Error(response.status === 404 ? '找不到這張名片' : '無法連線到名片服務')
  return response.status === 204 ? null : response.json()
}
function withInitials(card) { return { ...card, initials: initials(card.name) } }
async function loadShareOrigin() {
  try {
    const response = await fetch(`${apiBase}/share-config`)
    if (response.ok) shareOrigin.value = (await response.json()).frontendBaseUrl
  } catch { /* 本機 API 未提供分享網址時，沿用目前瀏覽器網址。 */ }
}
async function loadCards() {
  await loadShareOrigin()
  try {
    let result = await request()
    // [AI assisted chat-009] 首次啟動時將舊版瀏覽器範例資料匯入後端，避免使用者資料消失。
    const legacy = JSON.parse(localStorage.getItem('cardfolio-cards') || 'null')
    if (!result.length && legacy?.length) {
      result = await Promise.all(legacy.map(({ id, initials: _initials, ...card }) => request('', { method: 'POST', body: JSON.stringify({ ...card, displayFields: card.displayFields || ['email', 'phone', 'website'] }) })))
      localStorage.removeItem('cardfolio-cards')
    }
    cards.value = result.map(withInitials)
    selectedId.value = cards.value[0]?.id ?? null
  } catch (error) { notify(error.message) } finally { loading.value = false }
}
onMounted(loadCards)
function openCreate() { editingId.value = null; form.value = emptyForm(); modalOpen.value = true }
function openEdit(card) { editingId.value = card.id; form.value = { ...card }; modalOpen.value = true }
async function saveCard() {
  if (!form.value.name.trim() || !form.value.role.trim()) return
  try {
    const editing = editingId.value
    const card = withInitials(await request(editing ? `/${editing}` : '', { method: editing ? 'PUT' : 'POST', body: JSON.stringify(form.value) }))
    if (editing) cards.value = cards.value.map(item => item.id === card.id ? card : item)
    else { cards.value.unshift(card); selectedId.value = card.id }
    modalOpen.value = false
    notify(editing ? '名片已更新' : '新名片已建立')
  } catch (error) { notify(error.message) }
}
async function removeCard(card) {
  if (!confirm(`確定要刪除「${card.name}」這張名片嗎？`)) return
  try {
    await request(`/${card.id}`, { method: 'DELETE' })
    cards.value = cards.value.filter(item => item.id !== card.id)
    if (selectedId.value === card.id) selectedId.value = cards.value[0]?.id ?? null
    notify('名片已刪除')
  } catch (error) { notify(error.message) }
}
async function togglePublished(card) {
  try {
    const updated = withInitials(await request(`/${card.id}`, { method: 'PUT', body: JSON.stringify({ ...card, published: !card.published }) }))
    cards.value = cards.value.map(item => item.id === updated.id ? updated : item)
    notify(updated.published ? '名片已公開' : '名片已設為私人')
  } catch (error) { notify(error.message) }
}
function notify(message) { toast.value = message; setTimeout(() => toast.value = '', 2400) }
function cardUrl(card) { return `${shareOrigin.value}/card/${card.id}` }
function copyLink(card) { navigator.clipboard?.writeText(cardUrl(card)); notify('名片連結已複製') }
function qrUrl(card) { return `https://api.qrserver.com/v1/create-qr-code/?size=180x180&margin=4&data=${encodeURIComponent(cardUrl(card))}` }
const isPublicRoute = globalThis.location.pathname.startsWith('/card/')
const publicCardId = isPublicRoute ? globalThis.location.pathname.split('/').pop() : null
const publicCard = ref(null)
if (publicCardId) request(`/${publicCardId}`).then(card => { if (card.published) publicCard.value = withInitials(card) }).catch(() => {})
</script>

<template>
  <main v-if="isPublicRoute" class="public-card-page">
    <article v-if="publicCard" class="profile-card public-profile" :class="`theme-${publicCard.color}`">
      <span class="profile-brand">cardfolio<span>.</span></span><span class="profile-monogram">{{ publicCard.initials }}</span>
      <div class="profile-details"><div class="profile-name">{{ publicCard.name }}</div><div class="profile-role">{{ publicCard.role }}<template v-if="publicCard.company"> · {{ publicCard.company }}</template></div><div class="profile-rule"></div>
        <div class="profile-contact">
          <span v-for="field in publicCard.displayFields" :key="field" v-show="publicCard[field]">{{ ({ email: '✉', phone: '↗', website: '↗', location: '⌖' })[field] }} <span>{{ publicCard[field] }}</span></span>
        </div>
      </div><span class="profile-decoration deco-a"></span><span class="profile-decoration deco-b"></span>
    </article>
    <p v-else>找不到這張名片，或名片尚未公開。</p>
  </main>
  <div v-else class="app-shell">
    <aside class="sidebar">
      <a class="brand" href="#"><span class="brand-mark">c.</span><span>cardfolio<span class="brand-dot">.</span></span></a>
      <div class="workspace-label">WORKSPACE</div>
      <button class="nav-item active"><span class="nav-icon">▦</span> 我的名片 <span class="nav-count">{{ cards.length }}</span></button>
      <button class="nav-item" @click="notify('公開頁面即將推出')"><span class="nav-icon">◎</span> 公開頁面</button>
      <div class="side-bottom">
        <div class="side-tip"><span class="tip-spark">✳</span><strong>讓每一次交換<br>都留下好印象。</strong><p>一張名片，連結你的<br>所有專業可能。</p><span class="tip-orb orb-one"></span><span class="tip-orb orb-two"></span></div>
        <div class="profile-row"><div class="avatar-small">YL</div><div><strong>Yuan Lin</strong><small>個人工作區</small></div><span class="more">···</span></div>
      </div>
    </aside>

    <main class="main-content">
      <header class="topbar"><div class="breadcrumb">工作區 <span>/</span> <strong>我的名片</strong></div><div class="top-actions"><span class="saved-status"><i></i> 已自動儲存</span><button class="help-button" @click="notify('歡迎使用 Cardfolio')">?</button><div class="avatar-small">YL</div></div></header>
      <section class="page-heading"><div><div class="eyebrow"><span class="eyebrow-line"></span> YOUR DIGITAL IDENTITY</div><h1>名片管理<span class="heading-period">.</span></h1><p class="heading-copy">把你的每個專業面向，整理成恰到好處的第一印象。</p></div><button class="primary-button" @click="openCreate"><span>＋</span> 建立新名片</button></section>
      <section class="stats-row"><div class="stat-card"><div class="stat-icon lavender">▤</div><div><span>名片總數</span><strong>{{ cards.length }}<small> 張</small></strong></div><span class="stat-note">持續累積中</span></div><div class="stat-card"><div class="stat-icon green">↗</div><div><span>公開名片</span><strong>{{ publishedCount }}<small> 張</small></strong></div><span class="stat-note">隨時分享</span></div><div class="stat-card"><div class="stat-icon amber">⌁</div><div><span>數位名片</span><strong>100<small>%</small></strong></div><span class="stat-note">環保又便利</span></div></section>

      <div class="content-grid">
        <section class="library-panel"><div class="section-head"><div><h2>你的名片庫</h2><p>管理並打造不同場合的自己</p></div><label class="search-box"><span>⌕</span><input v-model="search" placeholder="搜尋名片..."></label></div>
          <div v-if="filteredCards.length" class="card-list"><button v-for="card in filteredCards" :key="card.id" class="business-card" :class="[`theme-${card.color}`, { chosen: selectedId === card.id }]" @click="selectedId = card.id"><span class="card-topline"><span class="mini-brand">c.</span><span class="card-type">DIGITAL CARD <i>✳</i></span></span><span class="card-identity"><span class="list-avatar">{{ card.initials }}</span><span><strong>{{ card.name }}</strong><small>{{ card.role }}<template v-if="card.company"> · {{ card.company }}</template></small></span></span><span class="card-bottom"><span class="visibility"><i :class="{ private: !card.published }"></i>{{ card.published ? '公開中' : '私人' }}</span><span class="card-date">{{ card.website || '尚未設定連結' }}</span></span><span v-if="selectedId === card.id" class="selected-check">✓</span></button></div>
          <div v-else class="empty-state"><span>⌕</span><strong>找不到符合的名片</strong><p>試試其他關鍵字，或建立一張新名片。</p></div>
          <button class="add-card-dashed" @click="openCreate"><span>＋</span><strong>新增一張名片</strong><small>為不同身份或場合建立專屬版本</small></button>
        </section>

        <section v-if="selected" class="preview-panel"><div class="preview-heading"><div><h2>即時預覽</h2><p>名片的數位樣貌</p></div><button class="icon-button" title="編輯名片" @click="openEdit(selected)">✎</button></div><div class="preview-stage"><div class="profile-card" :class="`theme-${selected.color}`"><span class="profile-brand">cardfolio<span>.</span></span><span class="profile-monogram">{{ selected.initials }}</span><div class="profile-details"><div class="profile-name">{{ selected.name }}</div><div class="profile-role">{{ selected.role }}<template v-if="selected.company"> · {{ selected.company }}</template></div><div class="profile-rule"></div><div class="profile-contact"><span v-if="selected.displayFields.includes('email') && selected.email">✉ <span>{{ selected.email }}</span></span><span v-if="selected.displayFields.includes('phone') && selected.phone">↗ <span>{{ selected.phone }}</span></span><span v-if="selected.displayFields.includes('website') && selected.website">↗ <span>{{ selected.website }}</span></span><span v-if="selected.displayFields.includes('location') && selected.location">⌖ <span>{{ selected.location }}</span></span></div></div><span class="profile-decoration deco-a"></span><span class="profile-decoration deco-b"></span></div><div class="stage-caption"><span class="live-dot"></span> 正面預覽 <span class="caption-sep">·</span> {{ selected.color === 'violet' ? '暮紫' : selected.color === 'mint' ? '薄荷' : '暖沙' }}配色</div></div>
          <div class="share-box"><div class="share-copy"><span class="share-icon">⌁</span><div><strong>分享你的名片</strong><p>掃描 QR code 或複製專屬連結</p></div></div><div class="share-content"><div class="qr-frame"><img :src="qrUrl(selected)" alt="名片 QR code"></div><div class="share-link"><span>專屬名片連結</span><div><code>{{ cardUrl(selected) }}</code><button @click="copyLink(selected)" title="複製連結">▢</button></div><button class="text-button" @click="togglePublished(selected)">{{ selected.published ? '設為私人名片' : '公開這張名片' }} <span>↗</span></button></div></div></div>
          <div class="quick-actions"><button @click="openEdit(selected)"><span>✎</span> 編輯名片</button><button @click="copyLink(selected)"><span>↗</span> 分享連結</button><button class="delete-action" @click="removeCard(selected)"><span>⌫</span> 刪除</button></div>
        </section>
        <section v-else class="preview-panel blank-preview"><div class="blank-icon">✳</div><h2>先建立你的第一張名片</h2><p>完成個人資訊後，就能即時預覽並分享。</p><button class="primary-button" @click="openCreate">＋ 建立新名片</button></section>
      </div>
      <footer class="footer"><span>© 2025 CARDFOLIO</span><span>你的專業，值得被好好介紹。 <b>✳</b></span></footer>
    </main>

    <div v-if="modalOpen" class="modal-backdrop" @click.self="modalOpen = false"><form class="edit-modal" @submit.prevent="saveCard"><div class="modal-top"><div><span class="eyebrow">MAKE IT YOURS</span><h2>{{ editingId ? '編輯名片' : '建立新名片' }}</h2><p>填入你的資訊，打造專屬數位名片。</p></div><button type="button" class="close-button" @click="modalOpen = false">×</button></div><div class="form-grid"><label class="full-field">姓名 <span>*</span><input v-model="form.name" required maxlength="60" placeholder="例如：林予安"></label><label>職稱 <span>*</span><input v-model="form.role" required maxlength="80" placeholder="例如：Product Designer"></label><label>公司 / 品牌<input v-model="form.company" maxlength="80" placeholder="例如：Studio North"></label><label>Email<input v-model="form.email" type="email" placeholder="hello@example.com"></label><label>電話<input v-model="form.phone" placeholder="+886 912 345 678"></label><label class="full-field">個人網站 / 社群<input v-model="form.website" placeholder="yourwebsite.com"></label><label class="full-field">所在地<input v-model="form.location" placeholder="Taipei, Taiwan"></label></div><div class="color-picker"><span>名片配色</span><button v-for="color in ['violet', 'mint', 'sand']" :key="color" type="button" :class="[`swatch-${color}`, { picked: form.color === color }]" @click="form.color = color" :aria-label="color"></button></div><fieldset class="display-fields"><legend>名片顯示欄位</legend><label v-for="item in [{ key: 'email', label: 'Email' }, { key: 'phone', label: '電話' }, { key: 'website', label: '網站' }, { key: 'location', label: '所在地' }]" :key="item.key"><input v-model="form.displayFields" type="checkbox" :value="item.key"> {{ item.label }}</label></fieldset><label class="publish-toggle"><input v-model="form.published" type="checkbox"><span class="toggle-ui"></span><span><strong>公開這張名片</strong><small>開啟後可透過連結和 QR code 分享</small></span></label><div class="modal-actions"><button type="button" class="cancel-button" @click="modalOpen = false">取消</button><button class="primary-button" type="submit">{{ editingId ? '儲存變更' : '建立名片' }} <span>→</span></button></div></form></div>
    <Transition name="toast"><div v-if="toast" class="toast-message"><span>✓</span>{{ toast }}</div></Transition>
  </div>
</template>
