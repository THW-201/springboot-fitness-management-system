import request from '@/api/request'
import { ApiResponse } from '@/api/request'

/**
 * 上传文件
 * @param file 要上传的文件
 * @param folder 可选的文件夹路径
 * @returns 上传成功后的文件路径
 */
export const uploadFile = async (file: File, folder?: string): Promise<string> => {
  const formData = new FormData()
  formData.append('file', file)

  const params = folder ? { folder } : {}

  const response = await request.post<ApiResponse<string>>('/files/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    params
  })

  return response.data.data
}

export default {
  uploadFile
}