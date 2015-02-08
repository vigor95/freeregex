package com.blueline.freeregex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 该类为freeregex的核心类，所有的功能主要分为三大类
 * 
 * <p>
 * 第一是正则编写函数 如：EMAIL等属性 和 anyOf(...) ，负责在其中填写正则表达式，几个属性都已经内置正则表达式
 * <p>
 * 第二是正则组合函数，为or(...)函数，可以将多个表达式用“或”拼接
 * <p>
 * 第三是功能函数，可以通过正则进行增删改查等多种操作
 * 
 * @author Iron Phoenix Hans
 */
public abstract class FreeMatcher {
	/**
	 * 一个包含EMAIL正则表达式的FreeMatcher对象
	 *
	 * <p>
	 * 内置了标准的EMAIL正则表达式，简化了在anyOf中编写的过程
	 */
	public static final FreeMatcher EMAIL = new FreeMatcher() {
		// 重写内部的getMatcher()
		@Override
		public Matcher getMatcher(CharSequence sequence) {
			Pattern pattern = Pattern
					.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
			return pattern.matcher(sequence);
		}
	};

	/**
	 * 一个包含IP正则表达式的FreeMatcher对象
	 *
	 * <p>
	 * 内置了标准的IP正则表达式，简化了在anyOf中编写的过程
	 */
	public static final FreeMatcher IP = new FreeMatcher() {
		// 重写内部的getMatcher()
		@Override
		public Matcher getMatcher(CharSequence sequence) {
			Pattern pattern = Pattern
					.compile("((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)");
			return pattern.matcher(sequence);
		}
	};

	/**
	 * 一个包含URL正则表达式的FreeMatcher对象
	 *
	 * <p>
	 * 内置了标准的URL正则表达式，简化了在anyOf中编写的过程
	 */
	public static final FreeMatcher URL = new FreeMatcher() {
		// 重写内部的getMatcher()
		@Override
		public Matcher getMatcher(CharSequence sequence) {
			Pattern pattern = Pattern.compile("[a-zA-z]+://[^\\s]*");
			return pattern.matcher(sequence);
		}
	};

	/**
	 * 一个包含中国大陆居民身份证号正则表达式的FreeMatcher对象
	 *
	 * <p>
	 * 内置了标准的中国大陆居民身份证号正则表达式，简化了在anyOf中编写的过程
	 */
	public static final FreeMatcher CHINA_ID = new FreeMatcher() {
		// 重写内部的getMatcher()
		@Override
		public Matcher getMatcher(CharSequence sequence) {
			Pattern pattern = Pattern.compile("\\d{15}(\\d\\d[0-9xX])?");
			return pattern.matcher(sequence);
		}
	};

	/**
	 * 返回一个包含正则表达式的FreeMatcher对象
	 * 
	 * @param regex
	 *            为正则表达式的字符串
	 * @return 一个包含正则表达式的FreeMatcher对象
	 */
	public static FreeMatcher anyOf(String regex) {
		return new FreeMatcher() {
			@Override
			public Matcher getMatcher(CharSequence sequence) {
				Pattern pattern = Pattern.compile(regex);
				return pattern.matcher(sequence);
			}
		};
	}

	/**
	 * 将两个包含正则表达式的FreeMatcher对象进行或链接
	 * 
	 * <p>
	 * 新的FreeMatcher对象，可以匹配旧的两个FreeMatcher对象中的任意一个
	 * 
	 * @param other
	 *            一个包含正则表达式的FreeMatcher对象
	 * @return 一个将两个FreeMatcher对象进行或链接的，包含正则表达式的FreeMatcher对象
	 */
	public FreeMatcher or(FreeMatcher other) {
		// 通过已经编译好的matcher得到最原始的regex string
		// 重构的关键点 是不是将原始的pattern保留？
		String regex = getMatcher("").pattern().pattern();
		String otherRegex = other.getMatcher("").pattern().pattern();
		return new FreeMatcher() {
			@Override
			public Matcher getMatcher(CharSequence sequence) {
				// 通过已经得到的两个字符串来拼装成一个新的字符串
				Pattern pattern = Pattern.compile(regex + "|" + otherRegex);
				return pattern.matcher(sequence);
			}
		};
	}

	/*
	 * //正则之间的 与 public FreeMatcher and(FreeMatcher other) { return null; }
	 * //正则之间的 非 public FreeMatcher not(FreeMatcher other) { return null; }
	 */

	// 全体机制实现的关键 1.在每次结果函数调用时都会调用一遍getMatcher()来获得matcher，从而通过对matcher操作得到结果
	// 2.在每次条件函数调用时重写getMatcher()
	abstract Matcher getMatcher(CharSequence sequence);

	/**
	 * 返回sequence是否与该对象匹配
	 * 
	 * <p>
	 * 判断sequence是否与该FreeMatcher对象包含的正则表达式匹配
	 * 
	 * @param sequence
	 *            一个字符序列的对象
	 * @return <tt>true</tt> sequence与该对象匹配， <tt>false</tt> sequence与该对象不匹配。
	 */
	public boolean matchesAllOf(CharSequence sequence) {
		return getMatcher(sequence).matches();
	}

	/**
	 * 返回sequence中是否有能与该对象匹配的部分
	 * 
	 * <p>
	 * 判断sequence是否有能与该FreeMatcher对象包含的正则表达式匹配的部分
	 * 
	 * @param sequence
	 *            一个字符序列的对象
	 * @return <tt>true</tt> sequence包含与该对象匹配的部分， <tt>false</tt>
	 *         sequence不包含与该对象匹配的部分。
	 */
	public boolean matchesAnyOf(CharSequence sequence) {
		Matcher m = getMatcher(sequence);
		m.reset();
		return m.find();
	}

	/**
	 * 返回sequence中第一个与该对象匹配的子串
	 * 
	 * <p>
	 * 寻找sequence中与该正则表达式匹配的第一个子串
	 * 
	 * @param sequence
	 *            一个字符序列的对象
	 * @return sequence中第一个与该对象匹配的子串， <tt>null</tt> sequence不包含与该对象匹配的部分。
	 */
	public String retainFirstFrom(CharSequence sequence) {
		return retainFirstFrom(sequence, 0);
	}

	/**
	 * 返回sequence中第一个与该对象匹配的子串的一部分
	 * 
	 * <p>
	 * 寻找sequence中与该正则表达式匹配的第一个子串的第group个分组
	 * 
	 * @param sequence
	 *            一个字符序列的对象
	 * @param group
	 *            正则表达式中分组的引索
	 * @return sequence中第一个与该对象匹配的子串， <tt>null</tt> sequence不包含与该对象匹配的部分。
	 */
	public String retainFirstFrom(CharSequence sequence, int group) {
		Matcher m = getMatcher(sequence);
		if (m.find()) {
			return m.group(group);
		}
		return null;
	}

	/**
	 * 返回sequence中全部与该对象匹配的子串
	 * 
	 * <p>
	 * 寻找sequence中与该正则表达式匹配的全部子串
	 * 
	 * @param sequence
	 *            一个字符序列的对象
	 * @return sequence中全部与该对象匹配的子串的list列表， 若list长度为0，则sequence不包含与该对象匹配的部分。
	 */
	public List<String> retainAllFrom(CharSequence sequence) {
		return retainAllFrom(sequence, 0);
	}

	/**
	 * 返回sequence中全部与该对象匹配的子串的一部分
	 * 
	 * <p>
	 * 寻找sequence中与该正则表达式匹配的全部子串的第group个分组
	 * 
	 * @param sequence
	 *            一个字符序列的对象
	 * @param group
	 *            正则表达式中分组的引索
	 * @return sequence中全部与该对象匹配的子串的list列表， 若list长度为0，则sequence不包含与该对象匹配的部分。
	 */
	public List<String> retainAllFrom(CharSequence sequence, int group) {
		Matcher m = getMatcher(sequence);
		List<String> list = new ArrayList<>();
		while (m.find()) {
			list.add(m.group(group));
		}
		return list;
	}

	/**
	 * 返回sequence中已被删除全部与该对象匹配的子串的新字符串
	 * 
	 * <p>
	 * 在sequence中删除全部与该对象匹配的子串
	 * 
	 * @param sequence
	 *            一个字符序列的对象
	 * @return sequence中已被删除全部与该对象匹配的子串的新字符串
	 */
	public String removeAllFrom(CharSequence sequence) {
		return getMatcher(sequence).replaceAll("");
	}

	/**
	 * 返回sequence中已被替换全部与该对象匹配的子串的新字符串
	 * 
	 * <p>
	 * 在sequence中将全部与该对象匹配的子串，替换成新字符串replacement
	 * 
	 * @param sequence
	 *            一个字符序列的对象
	 * @param replacement
	 *            替换后的字符串
	 * @return sequence中已被替换全部与该对象匹配的子串的新字符串
	 */
	public String replaceFrom(CharSequence sequence, String replacement) {
		return getMatcher(sequence).replaceAll(replacement);
	}

	/**
	 * 返回sequence中已被替换全部与该对象匹配的子串的新字符串
	 * 
	 * <p>
	 * 在sequence中将全部与该对象匹配的子串，利用replacement对象中的replacementMethod(String
	 * original)方法， 替换成新字符串
	 * 
	 * @param sequence
	 *            一个字符序列的对象
	 * @param replacement
	 *            替换规则对象
	 * @return sequence中已被替换全部与该对象匹配的子串的新字符串
	 */
	public String replaceFrom(CharSequence sequence, FreeReplacement replacement) {
		Matcher m = getMatcher(sequence);
		StringBuffer buf = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(buf, replacement.replacementMethod(m.group()));
		}
		m.appendTail(buf);
		return buf.toString();
	}

	/**
	 * 返回sequence中第一个与该对象匹配的子串的首位置
	 * 
	 * @param sequence
	 *            一个字符序列的对象
	 * @return sequence中第一个与该对象匹配的子串的首位置， <tt>-1</tt> sequence不包含与该对象匹配的部分。
	 */
	public int firstIndexIn(CharSequence sequence) {
		return firstIndexIn(sequence, 0);
	}

	/**
	 * 返回sequence中，从start位置开始， 第一个与该对象匹配的子串的首位置
	 * 
	 * @param sequence
	 *            一个字符序列的对象
	 * @param start
	 *            开始匹配的位置
	 * @return sequence中，从start位置开始， 第一个与该对象匹配的子串的首位置， <tt>-1</tt>
	 *         sequence不包含与该对象匹配的部分。
	 */
	public int firstIndexIn(CharSequence sequence, int start) {
		Matcher m = getMatcher(sequence);
		if (m.find(start)) {
			return m.start();
		} else {
			return -1;
		}
	}

	/**
	 * 返回sequence中，所有符合正则表达式的子串的首字符位置的list列表
	 * 
	 * @param sequence
	 *            一个字符序列的对象
	 * @return sequence中所有符合正则表达式的子串的首字符位置的list列表
	 *         若list长度为0，则sequence不包含与该对象匹配的部分。
	 */
	public List<Integer> allIndexIn(CharSequence sequence) {
		return allIndexIn(sequence, 0);
	}

	/**
	 * 返回sequence中，从start位置开始， 所有符合正则表达式的子串的首字符位置的list列表
	 * 
	 * @param sequence
	 *            一个字符序列的对象
	 * @param start
	 *            开始匹配的位置
	 * @return sequence中，从start位置开始， 所有符合正则表达式的子串的首字符位置的list列表
	 *         若list长度为0，则sequence不包含与该对象匹配的部分。
	 */
	public List<Integer> allIndexIn(CharSequence sequence, int start) {
		Matcher m = getMatcher(sequence);
		List<Integer> listIndex = new ArrayList<>();
		if (m.find(start)) {
			listIndex.add(m.start());
		}
		while (m.find()) {
			listIndex.add(m.start());
		}
		return listIndex;
	}

	/**
	 * 返回sequence中，有多少个符合正则表达式的子串
	 * 
	 * @param sequence
	 *            一个字符序列的对象
	 * @return sequence中，有多少个符合正则表达式的子串。
	 */
	public int countIn(CharSequence sequence) {
		return countIn(sequence, 0);
	}

	/**
	 * 返回sequence中，从start位置开始，有多少个符合正则表达式的子串
	 * 
	 * @param sequence
	 *            一个字符序列的对象
	 * @return sequence中，从start位置开始，有多少个符合正则表达式的子串。
	 */
	public int countIn(CharSequence sequence, int start) {
		Matcher m = getMatcher(sequence);
		int quantitySubString = 0;
		if (m.find(start)) {
			quantitySubString++;
		}
		while (m.find()) {
			quantitySubString++;
		}
		return quantitySubString;
	}
}
