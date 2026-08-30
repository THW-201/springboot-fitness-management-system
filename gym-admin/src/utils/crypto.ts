/**
 * 简单的加密/解密工具
 * 使用Base64编码进行简单加密(仅用于记住密码功能,不用于敏感数据)
 */

const SECRET_KEY = 'GEM_FITNESS_2024'

/**
 * 加密字符串
 */
export function encrypt(text: string): string {
  try {
    // 简单的异或加密 + Base64编码
    const encrypted = text.split('').map((char, i) => {
      const keyChar = SECRET_KEY.charCodeAt(i % SECRET_KEY.length)
      return String.fromCharCode(char.charCodeAt(0) ^ keyChar)
    }).join('')
    
    return btoa(encodeURIComponent(encrypted))
  } catch (error) {
    console.error('加密失败:', error)
    return ''
  }
}

/**
 * 解密字符串
 */
export function decrypt(encryptedText: string): string {
  try {
    const decoded = decodeURIComponent(atob(encryptedText))
    
    // 异或解密
    const decrypted = decoded.split('').map((char, i) => {
      const keyChar = SECRET_KEY.charCodeAt(i % SECRET_KEY.length)
      return String.fromCharCode(char.charCodeAt(0) ^ keyChar)
    }).join('')
    
    return decrypted
  } catch (error) {
    console.error('解密失败:', error)
    return ''
  }
}
