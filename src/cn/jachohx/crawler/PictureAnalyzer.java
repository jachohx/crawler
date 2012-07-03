package cn.jachohx.crawler;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

public class PictureAnalyzer {
	private static Map<String, String> charMarkMap = new HashMap<String, String>();
	
	// 灰度图时强度的阙值
	private static final int INTENSITY_THRESHOLD = 40; 
	
	/*  */
	private static final String LEFT_MARK = "0";
	private static final String RIGHT_MARK = "1";
	private static final String ANCHOR_MARK = "@";
	
	private static final String UNSURE_MARK = "*";
	private static final char UNSURE_CHAR = '*';
	
	// 字体像素点的标识
	public static final String PIXEL_MARK = "#";
	// 背景像素点的标识
	public static final String BACKGROUND_MARK = " ";
	
	// 其它像素点的标识
	private String otherPixelMark = "-";
		
	private BufferedImage sourceImage = null;
	
	public int sourceWidth = 0;
	public int sourceHeight = 0;
	
	// 图片的背景色强度和字体色强度
	private int backgroundIntensity = 255 / INTENSITY_THRESHOLD;
	private int fontIntensity = 0;
	
	// TODO 注释
	private int usualNumberWidth = 0;
	private int usualNumberHeight = 0;
	
	// TODO 需验证
	private int sizeCount = -1;
	
	private Logger logger = Logger.getLogger(PictureAnalyzer.class);
	
	
	// TODO 用于出错时的监控
	public StringBuilder monitorLogBuilder = new StringBuilder();
	
	// 分为状态和过程两类
	// 状态为=和@，停留符
	static{
		String serialization = null;
		
		// el:@01@
		serialization = ANCHOR_MARK + LEFT_MARK + RIGHT_MARK + ANCHOR_MARK;
		charMarkMap.put(serialization,		"0");
		
		// el:1
		serialization = RIGHT_MARK;
		charMarkMap.put(serialization,		"1");
		
		// el:@1@0@
		serialization = ANCHOR_MARK + RIGHT_MARK + ANCHOR_MARK + LEFT_MARK + ANCHOR_MARK;
		charMarkMap.put(serialization,		"2");
		
		// el:@1@1@
		serialization = ANCHOR_MARK + RIGHT_MARK + ANCHOR_MARK + RIGHT_MARK + ANCHOR_MARK;
		charMarkMap.put(serialization,		"3");
		
		// el:01@1
		serialization = LEFT_MARK + RIGHT_MARK + ANCHOR_MARK + RIGHT_MARK;
		charMarkMap.put(serialization,		"4");
		
		// el:@0@1@
		serialization = ANCHOR_MARK + LEFT_MARK + ANCHOR_MARK + RIGHT_MARK + ANCHOR_MARK;
		charMarkMap.put(serialization,		"5");
		
		// el:@0@01@
		serialization = ANCHOR_MARK + LEFT_MARK + ANCHOR_MARK + LEFT_MARK + RIGHT_MARK + ANCHOR_MARK;
		charMarkMap.put(serialization,		"6");
		
		// el:@1@0
		serialization = ANCHOR_MARK + RIGHT_MARK + ANCHOR_MARK + LEFT_MARK;
		charMarkMap.put(serialization,		"7");
		
		// el:@01@01@
		serialization = ANCHOR_MARK + LEFT_MARK + RIGHT_MARK + ANCHOR_MARK + LEFT_MARK + RIGHT_MARK + ANCHOR_MARK ;
		charMarkMap.put(serialization,		"8");
		
		// el:@01@1@
		serialization = ANCHOR_MARK + LEFT_MARK + RIGHT_MARK + ANCHOR_MARK + RIGHT_MARK + ANCHOR_MARK;
		charMarkMap.put(serialization,		"9");
		
		// el:@
		serialization = ANCHOR_MARK;
		charMarkMap.put(serialization,		".");
		
		/* 以下为不规范的标识 */
		charMarkMap.put("@1@",		"3");
		charMarkMap.put("1@01@1",	"4");
		charMarkMap.put("@01@1",	"9");
		charMarkMap.put("0@01@",	"6");
		charMarkMap.put("@1",	"7");
		
	}
	
	public String getOtherPixelMark() {
		return otherPixelMark;
	}

	public void setOtherPixelMark(String otherPixelMark) {
		this.otherPixelMark = otherPixelMark;
	}
	
	public void deleteOtherPixelMark() {
		this.otherPixelMark = PIXEL_MARK;
	}
			
	// TODO 必须统计的数据，如：线的宽度
		
	// 清除特定的统计
	public void clear(){
		monitorLogBuilder = new StringBuilder();
		sourceImage = null;
		sourceWidth = 0;
		sourceHeight = 0;
		usualNumberWidth = 0;
		usualNumberHeight = 0;
		sizeCount = -1;
	}
	
	// ====================================================================================================
		
	public String analyseImage(){
		if(sourceImage == null){
			logger.debug(monitorLogBuilder.toString() + " It's no even inited the image!");
			return "";
		}
		StringBuilder resultStr = new StringBuilder();
		
		BufferedImage grayImage = ChangeGrayImage(sourceImage);
		analyseImage_Gray(grayImage);
		printThumbnail(grayImage);
		List<BufferedImage> numberImageList = getNumberImage(grayImage);
				
		for(BufferedImage numberImage : numberImageList){
			printThumbnail(numberImage);
			String number = analysisNumber(numberImage);
			
			if(UNSURE_MARK.equals(number)){
				// TODO 精确没有结果时，应该进行模糊
			}
			resultStr.append(number);
		}
		
		monitorLogBuilder.append("原始结果：");
		monitorLogBuilder.append(resultStr.toString());
		return changeNumber(resultStr);
	}
	
	public String changeNumber(StringBuilder targetBuilder){
		if(UNSURE_CHAR == targetBuilder.charAt(0)){
			targetBuilder.deleteCharAt(0);
		}
		
		if(UNSURE_CHAR == targetBuilder.charAt(targetBuilder.length() - 1)){
			targetBuilder.deleteCharAt(targetBuilder.length() - 1);
		}
		
		String numStr = targetBuilder.toString();
		
		// 死方法，专门应对宋体全符“1”的情况;进一步破坏了通用性，后人自戒；
		if(numStr.replace(".", "").length() < numStr.length() - 1){
			int realPointOffset = numStr.lastIndexOf(".");
			numStr = numStr.substring(0, realPointOffset).replace(".", "1") + numStr.substring(realPointOffset);
		}
		
		
		try{
			float num = Float.parseFloat(numStr);
			num = ((int)num * 10) / 10;
			numStr = String.valueOf(num);
		}catch(NumberFormatException nException){
			logger.error(monitorLogBuilder.toString() + " It isn't a nomal number!"); 
			return null;
		}
		if("".equals(numStr)){
			logger.info("The result is null. You can check the log:" + monitorLogBuilder.toString());
		}
		return numStr;
	}
	
	// ====================================================================================================
	
	/**
	 * 去色
	 * @param sourceImage
	 * @return
	 */
	public BufferedImage ChangeGrayImage(BufferedImage sourceImage){	
		BufferedImage bImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics bg = bImage.getGraphics();
        bg.drawImage(sourceImage, 0, 0, null);
        bg.dispose();
        sourceImage = null;
        return bImage;
	}
	
	
	
	private String[][] getGrayImageArray(BufferedImage targetImage, int widthOffset, int heightOffset){
				
		int imageWidth = targetImage.getWidth();
		int imageHeight = targetImage.getHeight();
		
		String[][] imageArray = new String[imageWidth + widthOffset][imageHeight + heightOffset];
		
		// 布景
		for(int lineX = 0; lineX < imageWidth + widthOffset; lineX++ ){
			for(int lineY = 0; lineY < imageHeight + heightOffset; lineY++ ){
				imageArray[lineX][lineY] = BACKGROUND_MARK;
			}
		}
		
		for(int lineX = 0; lineX < imageWidth; lineX++ ){
			for(int lineY = 0; lineY < imageHeight; lineY++ ){
			    int pixel=(targetImage.getRGB(lineX, lineY) & 0xff);
			    pixel = pixel / INTENSITY_THRESHOLD;
			    
			    if(pixel == backgroundIntensity){
			    	imageArray[widthOffset + lineX][heightOffset + lineY] = BACKGROUND_MARK;
			    	continue;
			    }
			    if(pixel == fontIntensity){
			    	imageArray[widthOffset + lineX][heightOffset + lineY] = PIXEL_MARK;
			    	continue;
			    }
			    
			    // 噪音颜色，避免与背景、字体的标识碰撞
			    imageArray[widthOffset + lineX][heightOffset + lineY] = otherPixelMark;
			}
		}
		
		return imageArray;
	}
	
	
	public int[] getGrayImageOffset(int imageWidth, int imageHeight){
		int targetWidth = usualNumberWidth;
		int targetHeight = usualNumberHeight;
		
		// TODO 判断，要求的尺寸是否合理，不合理，则去掉要求尺寸
		if( (targetWidth == 0 || targetHeight == 0) ||
			(targetWidth < imageWidth || targetHeight < imageHeight) || 
			(imageWidth * 1.5 > targetWidth && imageHeight * 1.5 > targetHeight) ||
			(targetWidth / 2) >= imageWidth && (targetHeight / 2) >= imageHeight
		){
			targetWidth = imageWidth;
			targetHeight = imageHeight;
		}else{
			// 保证尺寸在原始，原始2倍和定制三种，原始2倍优先级高于定制
			if(targetWidth < (2 * imageWidth)){
				targetWidth = 2 * imageWidth + 1;
			}
			
			if(targetHeight < (2 * imageHeight)){
				targetHeight = 2 * imageHeight + 1;
			}
		}
		
		// 位置纠正的偏移量
		int widthOffset = targetWidth - imageWidth;
		int heightOffset = targetHeight - imageHeight;
		
		int[] X_Y_Offset = {widthOffset, heightOffset};
		
		return X_Y_Offset;
	}
	
	/**
	 * 统计图片(Gray)的背景色和字体色
	 * @param targetImage
	 */
	public void analyseImage_Gray(BufferedImage targetImage){
		int width = targetImage.getWidth();
		int height = targetImage.getHeight();
		
		int[] countArray = new int[(255 / INTENSITY_THRESHOLD + 1)];
		
		for(int lineX = 0; lineX < width; lineX++ ){
			for(int lineY = 0; lineY < height; lineY++ ){
			    int pixel=targetImage.getRGB(lineX, lineY);
			    int intensity = (pixel) & 0xff;
			    // TODO 游标需重新修改
			    countArray[intensity / INTENSITY_THRESHOLD]++;
			}
		}
		
		int backgroundMaxCount = -1;
		int fontMaxCount = -1;
		
		int backgroundIntensity = -1;
		int fontIntensity = -1;
		
		for(int index = 0; index < countArray.length; index++){
			if(countArray[index] > backgroundMaxCount){
				fontMaxCount = backgroundMaxCount;
				fontIntensity = backgroundIntensity;
				backgroundMaxCount = countArray[index];
				backgroundIntensity = index;
				continue;
			}
			if(countArray[index] > fontMaxCount){
				fontMaxCount = countArray[index]; 
				fontIntensity = index;
			}
		}

		
		if(backgroundIntensity != -1){
			this.backgroundIntensity = backgroundIntensity;
			if(fontIntensity != -1){
				this.fontIntensity =fontIntensity;
			}
		}
		
	}
	
	
	public List<BufferedImage> XCutImage(BufferedImage targetImage){
		List<BufferedImage> imageList = new ArrayList<BufferedImage>();
		int width = targetImage.getWidth();
		int height = targetImage.getHeight();
		String[][] map = getGrayImageArray(targetImage, 0, 0);
		
		int standLineX = 0;
		
		for(int lineX = 0; lineX < width; lineX++ ){
			Boolean YHasPoint = false;
			Boolean nYHasPoint = false;
			Boolean isCountinuous = false;
			for(int lineY = 0; lineY < height; lineY++ ){
				 if(PIXEL_MARK.equals(map[lineX][lineY])){
					 YHasPoint = true;
				 }
				 if((lineX + 1 )< width){
					 if(PIXEL_MARK.equals(map[lineX + 1][lineY])){
						 nYHasPoint = true;
					 }
					 
					 if(PIXEL_MARK.equals(map[lineX + 1][lineY]) && PIXEL_MARK.equals(map[lineX][lineY])){
						 isCountinuous = true;
						 break;
					 }
				 }
				 
			}
			if(!isCountinuous){
				if(YHasPoint){
					BufferedImage newImage = targetImage.getSubimage(standLineX, 0, lineX - standLineX + 1, height);
					imageList.add(newImage);
				}
				if(nYHasPoint){
					standLineX = lineX + 1;
				}
			}
		}
				
		return imageList;
	}
	
	public BufferedImage YCutBorder(BufferedImage targetImage){
		int width = targetImage.getWidth();
		int height = targetImage.getHeight();
		String[][] map = getGrayImageArray(targetImage, 0 , 0);
				
		int top = height;
		int bottom = -1;
				
		for(int lineY = 0; lineY < height; lineY++ ){
			for(int lineX = 0; lineX < width; lineX++ ){
				if(PIXEL_MARK.equals(map[lineX][lineY])){	
					top = lineY;
					break;
				}
			}
			if(top != height){
				break;
			}
		}
		
		for(int lineY = height - 1; lineY >= top; lineY-- ){
			for(int lineX = 0; lineX < width; lineX++ ){
				if(PIXEL_MARK.equals(map[lineX][lineY])){
						bottom = lineY;
						break;
				}
			}
			if(bottom != -1){
				break;
			}
		}
		
		if(top == -1 || top > bottom){
			logger.warn("Image'size is unnormal!");
			return null;
		}
		
		BufferedImage newImage = targetImage.getSubimage(0, top, width, bottom - top + 1);
		
		
		// 方法去除过大或过小的size影响，取最普遍，且时间、空间复杂度低
		if((usualNumberWidth == width) && (usualNumberHeight == bottom - top + 1)){
			sizeCount++;
		}else{
			sizeCount --;
		}
		
		if(sizeCount < 0){
			if(sizeCount < -1 || ((usualNumberWidth < width) && (usualNumberHeight < bottom - top + 1))){
				usualNumberWidth = width;
				usualNumberHeight = bottom - top + 1;
				sizeCount = 0;
			}
		}
		
		/*
		if(usualNumberWidth < width){
			usualNumberWidth = width;
		}
		if(usualNumberHeight < bottom - top + 1){
			usualNumberHeight = bottom - top + 1;
		}
		*/
		
		return newImage;
	}
	
	
	public List<BufferedImage> getNumberImage(BufferedImage targetImage){
		
        
		List<BufferedImage> imageList = null;
		
		imageList = XCutImage(targetImage);
		
		
		List<BufferedImage> finalImageList = new ArrayList<BufferedImage>();
		for(BufferedImage image : imageList){
			finalImageList.add(YCutBorder(image));
		}
				
		return finalImageList;
	}
	
	// ====================================================================================================
	
	public String analysisNumber(BufferedImage numberImage){
		List<String> serialization = null;
		int[] X_Y_Offset = getGrayImageOffset(numberImage.getWidth(), numberImage.getHeight());		
		serialization = getOriginSerialization(getGrayImageArray(numberImage, X_Y_Offset[0], X_Y_Offset[1]));
		serialization = serializationNormalizing(serialization);
		
		StringBuilder markStr = new StringBuilder();
		
		for(String mark : serialization){
			markStr.append(mark);
		}
		
		Iterator<String> keyIterator = charMarkMap.keySet().iterator();
		int corruntLength = -1;
		String result = charMarkMap.get(markStr.toString());
		if(result == null){
			while(keyIterator.hasNext()){
				String tmpKey = keyIterator.next();
				if(markStr.toString().contains(tmpKey) && corruntLength < tmpKey.length() && tmpKey.length() > 1){
					corruntLength = tmpKey.length();
					result = charMarkMap.get(tmpKey);
				}
			}
		}
						
		if(result == null){
			result = UNSURE_MARK;
		}
		
		return result;
	}
	
	/**
	 * 序列化
	 * @param map
	 * @param topLineMark
	 * @param middleLineMark
	 * @param bottemLineMark
	 */
	public List<String> getOriginSerialization(String[][] map){
		List<String> serialization = new ArrayList<String>();
		
		int height = map[0].length;
		
		String[] lastLine = null;
		String lastState = null;
		for(int lineY = 0; lineY < height; lineY++){
			String state = null;
			
			String[] tmpArray = new String[map.length];
			for(int lineX = 0; lineX < map.length; lineX++){
				tmpArray[lineX] = map[lineX][lineY];
			}
			
			state = getLineState(tmpArray);
			
			// TODO 只有一行的，会被当做干扰去掉。
			if(state.length() != 0){
				if(lastLine != null && ((LEFT_MARK + RIGHT_MARK).equals(lastState) || (LEFT_MARK + RIGHT_MARK).equals(state)) && (!ANCHOR_MARK.equals(lastState) && !ANCHOR_MARK.equals(state))){
					if(isContinuous(lastLine, tmpArray)){
						serialization.remove(serialization.size() -1);
						serialization.add(ANCHOR_MARK);
					}else{
						serialization.add(state);
					}
				}else{
					serialization.add(state);
				}
			}
			
			lastLine = tmpArray;
			lastState = state;
		}
		
		return serialization;
	}
		
	public String getLineState(String[] line){
		StringBuilder resultState = new StringBuilder();
		
		int length = line.length;
		int halver  = line.length / 2;
		//int trisection = line.length / 3;
//		float quarter = line.length / 4f;
		
		int subLineLength = 0;
		int maxSubLineLength = 0;
		
		for(int index = 0; index <= line.length; index++){
			if(index < line.length && !BACKGROUND_MARK.equals(line[index]) ){
				subLineLength++;
			}else{
				if(subLineLength != 0){
					
					if(index > halver && (index - subLineLength) < halver && subLineLength > halver){
						resultState.append(ANCHOR_MARK);
					}else{
						if(2 * index - subLineLength < length){
							if(resultState.indexOf(LEFT_MARK) == -1){
								resultState.append(LEFT_MARK);
							}
						}
						if(2 * index - subLineLength > length){
							if(resultState.indexOf(ANCHOR_MARK) != -1 && resultState.indexOf(LEFT_MARK) == -1){
								resultState.deleteCharAt(resultState.indexOf(ANCHOR_MARK));
								resultState.append(LEFT_MARK);
							}
							if(resultState.indexOf(RIGHT_MARK) == -1){
								resultState.append(RIGHT_MARK);
							}
						}
						if(2 * index - subLineLength == length){
							if(resultState.indexOf(LEFT_MARK) != -1){
								resultState.append(RIGHT_MARK);
							}else{
								resultState.append(ANCHOR_MARK);
							}
							
						}
					}
									
					if(subLineLength > maxSubLineLength){
						maxSubLineLength = subLineLength;
					}
					subLineLength = 0;
				}
			}
			
		}
		
		
		
		return resultState.toString();
	}
		
	public List<String> serializationNormalizing(List<String> serialization){
		monitorLogBuilder.append("原mark:");
		for(String mark : serialization){
			monitorLogBuilder.append(mark + " ");
		}
		monitorLogBuilder.append("\n");
		
		serializationSimplify(serialization);
		
		monitorLogBuilder.append("后mark:");
		for(String mark : serialization){
			monitorLogBuilder.append(mark + " ");
		}
		monitorLogBuilder.append("\n");
		
		serialization = serializationCompile(serialization);
				
		
		monitorLogBuilder.append("归一后的mark:");
		for(String mark : serialization){
			monitorLogBuilder.append(mark + " ");
		}
		monitorLogBuilder.append("\n");
		
		return serialization;
	}
	
	public void serializationSimplify(List<String> serialization){
		
		for(int index = 0; index < serialization.size() - 1; index++){
			if(serialization.get(index).equals(serialization.get(index + 1))){
				serialization.remove(index);
				index--;
				continue;
			}			
		}
		
	}
	
	
	public void serializationCompile_Fake(List<String> serialization, int start, int end){
		int index = start;
		while( index < serialization.size() ){
			String leftState = serialization.get(index);
			if(ANCHOR_MARK.equals(leftState)){
				index++;
				continue;
			}
			
			if((index + 1) < serialization.size()){
				String rightState = serialization.get(index + 1);
				if(ANCHOR_MARK.equals(rightState)){
					index++;
					continue;
				}
				
				Boolean flag = true;	
				
				if(leftState.contains(rightState) && flag){
					serialization.remove(index);
					flag =false;
				}
				if(rightState.contains(leftState) && flag){
					serialization.remove(index + 1);
					flag =false;
				}
								
				if(flag){
					serialization.add(index + 1, ANCHOR_MARK);
					index += 2;
					flag =false;
				}
			}else{
				index++;
			}
		}
	}
	
	
	
	public List<String> serializationCompile_Fake_test(List<String> serialization, int start, int end){
		List<String> insertedSerialization = new ArrayList<String>();
		if(end > serialization.size()){
			return insertedSerialization;
		}
		
		int index = start + 1;
		
		insertedSerialization.add(serialization.get(index));
		
		
		while( index < end ){
			String leftState = insertedSerialization.get(insertedSerialization.size() - 1);
			
			if((index + 1) < end){
				String rightState = serialization.get(index + 1);
				
				Boolean flag = true;	
				
				if(leftState.contains(rightState) && flag){
					insertedSerialization.remove(insertedSerialization.size() - 1);
					insertedSerialization.add(rightState);
					flag =false;
				}
				if(rightState.contains(leftState) && flag){
					flag =false;
				}
				
				if(flag){
					insertedSerialization.add(ANCHOR_MARK);
					insertedSerialization.add(rightState);
				}
			}
			index++;
		}
				
		return insertedSerialization;
	}
	
	public Boolean isContinuous(String[] lastLine, String[] nextLine){
		
		if(lastLine.length != nextLine.length){
			logger.error("Two rows'length aren't euqual!");
			return false;
		}
		for(int index = 0; index < lastLine.length; index++){
			if(PIXEL_MARK.equals(nextLine[index])){
				lastLine[index] = PIXEL_MARK; 
			}
		}
		
		String mergeState = getLineState(lastLine);
		
		if(ANCHOR_MARK.equals(mergeState)){
			return true;
		}
		
		return false;
	}
	
	public List<String> serializationCompile(List<String> serialization){
		
		List<String> resultSerialization = new ArrayList<String>();
		
		int index = 0;
		
		while( index < serialization.size() ){
			String leftState = serialization.get(index);
			int lastAnchor = -1;
			int nextAnchor = -1;
			if(ANCHOR_MARK.equals(leftState)){
				lastAnchor = index;
				resultSerialization.add(ANCHOR_MARK);
			}
			
			for(int nextIndex = index + 1; nextIndex < serialization.size(); nextIndex++){
				String rightState = serialization.get(nextIndex);
				if(ANCHOR_MARK.equals(rightState)){
					nextAnchor = nextIndex;
					break;
				}
			}
			
			if(lastAnchor == -1 && nextAnchor != -1){
				resultSerialization.add(serialization.get(nextAnchor - 1));
			}
			
			if(lastAnchor != -1 && nextAnchor == -1){
				if(lastAnchor + 1 < serialization.size()){
					resultSerialization.add(serialization.get(lastAnchor + 1));
				}
				break;
			}
			
			if(lastAnchor == -1 && nextAnchor == -1){
				return serialization;
			}
			
			if(lastAnchor != -1 && nextAnchor != -1){
				resultSerialization.addAll(serializationCompile_Fake_test(serialization, lastAnchor, nextAnchor));
			}
			index = nextAnchor;
		}
				
		return resultSerialization;
	}
			
	/**
	 * Load Image
	 * @param file
	 */
	public Boolean initImage(File file) throws IOException{
		sourceImage = ImageIO.read(file);
		monitorLogBuilder.append(file.getPath());
		
		if(sourceImage == null){
			logger.warn("The object don't bring a normal picture:" + monitorLogBuilder.toString());
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * Load Image
	 * @param file
	 */
	public Boolean initImage(URL url) throws IOException{
		sourceImage = ImageIO.read(url);
		monitorLogBuilder.append(url.toString());
		
		if(sourceImage == null){
			logger.warn("The object don't bring a normal picture:" + monitorLogBuilder.toString());
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * Load Image
	 * @param file
	 */
	public Boolean initImage(InputStream input) throws IOException{
		sourceImage = ImageIO.read(input);
		monitorLogBuilder.append("InputStream");
		
		if(sourceImage == null){
			logger.warn("The object don't bring a normal picture:" + monitorLogBuilder.toString());
			return false;
		}else{
			return true;
		}
	}
		
	/**
	 * 打印图片点阵
	 * @param targetImage
	 */
	public void printThumbnail(BufferedImage targetImage){
		int width = targetImage.getWidth();
		int height = targetImage.getHeight();
		monitorLogBuilder.append("\n");
		monitorLogBuilder.append("<Thumbnail>");
		monitorLogBuilder.append("\n");
		String[][] thumbnailArray = getGrayImageArray(targetImage, 0, 0);
		for(int lineY = 0; lineY < height; lineY++ ){
			for(int lineX = 0; lineX < width; lineX++ ){
				monitorLogBuilder.append(thumbnailArray[lineX][lineY]);
			}
			monitorLogBuilder.append("\n");
		}
		monitorLogBuilder.append("</Thumbnail>");
	}	
}