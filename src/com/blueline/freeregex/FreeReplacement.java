package com.blueline.freeregex;

/**
 * 调用FreeMatcher中的replaceFrom(CharSequence sequence, FreeReplacement replacement)时，必须实现该接口
 * 
 * @author Iron Phoenix Hans
 */
public interface FreeReplacement {
	/**
	 * 将字符序列中符合正则表达式的子串，通过实现的方法改变
	 * 
	 * <p>
	 * 调用FreeMatcher中的replaceFrom(CharSequence sequence, FreeReplacement replacement)时
	 * 第二个参数为该接口的实现类，可以通过实现该方法，编写特定的替换规则，实现基于原子串的替换
	 * 
	 * @param original
	 *            在字符序列中符合正则表达式的子串
	 * @return 改变后的字符串
	 */
	String replacementMethod(String original);
}
