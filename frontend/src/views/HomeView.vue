<template>
  <div class="relative min-h-screen bg-gray-900 text-white overflow-hidden">
    <WarpBackground>
      <div class="container mx-auto px-4 py-12 flex flex-col items-center">
        <div class="text-center">
          <AnimatedGradientText class="text-3xl md:text-4xl font-bold mb-6">
            Documentation Management
          </AnimatedGradientText>
          <TypingAnimation
            :text="['Transform invoice images into structured excel files.']"
            :loop="true"
            :speed="50"
            class="text-xl md:text-2xl text-gray-300"
          />
        </div>
      </div>
    </WarpBackground>
    
    <div class="bg-white rounded-xl shadow-lg overflow-hidden p-6 w-full max-w-4xl mx-auto my-8">
      <h2 class="text-2xl font-bold mb-6 text-gray-800">Intelligent Invoice Processing</h2>
      <div class="flex flex-col gap-8 items-center">
        <!-- Upload Invoice Section -->
        <div class="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden p-6 w-full">
          <h3 class="text-xl font-semibold mb-4 text-gray-800">Upload Invoice</h3>
          
          <div
            class="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center"
            :class="{ 'border-blue-500 bg-blue-50': isDragging }"
            @dragover.prevent="isDragging = true"
            @dragleave.prevent="isDragging = false"
            @drop.prevent="handleDrop"
          >
            <div v-if="!selectedFile">
              <svg class="mx-auto h-12 w-12 text-gray-400" stroke="currentColor" fill="none" viewBox="0 0 48 48">
                <path d="M28 8H12a4 4 0 00-4 4v20m32-12v8m0 0v8a4 4 0 01-4 4H8m36-12h-4m4 0H20" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
              <p class="mt-1 text-sm text-gray-500">
                Drag and drop your invoice, or <span class="text-blue-500 cursor-pointer" @click="$refs.fileInput.click()">browse</span>
              </p>
              <p class="mt-1 text-xs text-gray-400">PDF, PNG, JPG up to 10MB</p>
            </div>
            
            <div v-else class="flex flex-col items-center">
              <div class="flex items-center justify-center w-12 h-12 rounded-full bg-blue-100">
                <svg class="h-6 w-6 text-blue-500" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4zm2 6a1 1 0 011-1h6a1 1 0 110 2H7a1 1 0 01-1-1zm1 3a1 1 0 100 2h6a1 1 0 100-2H7z" clip-rule="evenodd" />
                </svg>
              </div>
              <p class="mt-2 text-sm font-medium text-gray-900">{{ selectedFile.name }}</p>
              <p class="text-sm text-gray-500">{{ formatFileSize(selectedFile.size) }}</p>
              <button
                @click="selectedFile = null"
                class="mt-2 text-xs text-red-500 hover:text-red-700 focus:outline-none"
              >
                Remove
              </button>
            </div>
            
            <input
              ref="fileInput"
              type="file"
              class="hidden"
              accept=".pdf,.png,.jpg,.jpeg"
              @change="handleFileChange"
            />
          </div>
          
          <div class="mt-4 flex flex-col gap-4">
            <div class="flex items-center">
              <label for="language" class="block text-sm font-medium text-gray-700 mr-3">Language:</label>
              <select
                id="language"
                v-model="language"
                class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 text-sm text-gray-900"
              >
                <option value="eng">English</option>
                <option value="ita">Italian</option>
              </select>
            </div>
            
            <button
              @click="processInvoice"
              :disabled="isProcessing || !selectedFile"
              class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
              :class="{ 'opacity-50 cursor-not-allowed': isProcessing || !selectedFile }"
            >
              <span v-if="isProcessing">
                Processing...
                <svg class="animate-spin ml-2 h-4 w-4 text-white inline" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
              </span>
              <span v-else>Process Invoice</span>
            </button>
          </div >
        </div>

        <!-- Recent Invoices Section -->
        <div class="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden p-6 w-full mt-8">
          <h3 class="text-xl font-semibold mb-4 text-gray-800">Recent Invoices</h3>
          <div v-if="recentInvoices.length === 0" class="text-gray-500 text-center py-4">
            No documents found.
          </div>
          <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <div v-for="invoice in recentInvoices" :key="invoice.id" class="border border-gray-200 rounded-lg p-4 shadow-sm">
              <p class="font-medium text-gray-900">{{ invoice.filename }}</p>
              <p class="text-sm text-gray-600">Date: {{ invoice.date }}</p>
              <p class="text-sm text-gray-600">Total: {{ invoice.totalAmount }} {{ invoice.currency }}</p>
              <!-- Add more invoice details as needed -->
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'
import WarpBackground from '@/components/WarpBackground.vue'
import AnimatedGradientText from '@/components/AnimatedGradientText.vue'
import TypingAnimation from '@/components/TypingAnimation.vue'

const router = useRouter()
const fileInput = ref(null)
const selectedFile = ref(null)
const isDragging = ref(false)
const isProcessing = ref(false)
const language = ref('eng')
const recentInvoices = ref([])

const handleDrop = (e) => {
  isDragging.value = false
  const files = e.dataTransfer.files
  if (files.length > 0) {
    const file = files[0]
    if (isValidFile(file)) {
      selectedFile.value = file
    }
  }
}

const handleFileChange = (e) => {
  const files = e.target.files
  if (files.length > 0) {
    selectedFile.value = files[0]
  }
}

const isValidFile = (file) => {
  const validTypes = ['application/pdf', 'image/png', 'image/jpeg']
  const maxSize = 10 * 1024 * 1024 // 10MB
  return validTypes.includes(file.type) && file.size <= maxSize
}

const formatFileSize = (bytes) => {
  if (bytes < 1024) return bytes + ' bytes'
  else if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB'
  else return (bytes / 1048576).toFixed(1) + ' MB'
}

const processInvoice = async () => {
  if (!selectedFile.value) return
  
  isProcessing.value = true
  try {
    const response = await api.processToExcel(selectedFile.value)
    router.push({
      name: 'processing',
      query: { jobId: response.data.jobId }
    })
  } catch (error) {
    console.error('Error processing invoice:', error)
    alert('Failed to process invoice. Please try again.')
  } finally {
    isProcessing.value = false
  }
}

onMounted(async () => {
  try {
    const response = await api.getInvoices()
    recentInvoices.value = response.data || []
  } catch (error) {
    console.error('Error fetching invoices:', error)
  }
})
</script>
