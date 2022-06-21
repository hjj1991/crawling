import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {


    private static final List<String> guList = new ArrayList<>(Arrays.asList(new String[]{"강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구", "노원구", "도봉구", "동대문구", "동작구", "마포구", "서대문구", "서초구", "성동구", "성북구", "송파구", "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구"}));

    public static void main(String[] args) throws InterruptedException {

        searchValue(guList.get(0));
    }

    public static void searchValue(String value) throws InterruptedException {
        //세션 시작
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--enable-javascript");
        //페이지가 로드될 때까지 대기
        //Normal: 로드 이벤트 실행이 반환 될 때 까지 기다린다.
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
//        options.addArguments("no-sandbox");
//        options.addArguments("disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(10));

        driver.get("https://map.naver.com/v5");


        /* 검색창에 입력값 대입 */

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("input_search")));
        driver.findElement(By.className("input_search")).sendKeys(value + "바버샵");
        driver.findElement(By.className("input_search")).sendKeys(Keys.ENTER);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("searchIframe")));
        driver.switchTo().frame(driver.findElement(By.id("searchIframe")));

        WebElement item = driver.findElement(By.id("_pcmap_list_scroll_container"));
        long currentTime = new Date().getTime();

        while (new Date().getTime() < currentTime + 5000){
            Thread.sleep(500);
            ((JavascriptExecutor)driver).executeScript("arguments[0].scrollBy(0,2000)", item);
        }

//        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("place_bluelink")));

        List<WebElement> list = driver.findElements(By.className("_22p-O"));
        List<ShopInfo> barbershopList = new ArrayList<>();

        for (WebElement webElement : list) {
            ShopInfo shopInfo = new ShopInfo();
            webElement.findElement(By.className("place_bluelink")).click();

            driver.switchTo().defaultContent();
            Thread.sleep(1000);

            driver.switchTo().frame(driver.findElement(By.id("entryIframe")));
            Thread.sleep(1000);


            ((JavascriptExecutor)driver).executeScript("window.scrollTo(0, 2000)", driver.findElement(By.id("app-root")));
            Thread.sleep(1000);

            shopInfo.setShopName(driver.findElement(By.className("_3XamX")).getText());

            try{
                shopInfo.setTel(driver.findElement(By.className("_3ZA0S")).getText());
            }catch (Exception e){
                shopInfo.setTel("");
            }

            try {
                shopInfo.setAddress(driver.findElement(By.className("_2yqUQ")).getText());
            }catch (Exception e){
                shopInfo.setAddress("");
            }

            try {
                List<WebElement> priceList = driver.findElements(By.className("_2hjMG"));
                for (WebElement element : priceList) {
                    if(element.findElement(By.className("_1kuzz")).getText().contains("컷")){
                        shopInfo.setPrice(element.findElement(By.className("_2QEvg")).getText());
                    }
                }
            }catch (Exception e){
                shopInfo.setPrice("");
            }

            try {
                List<String> nameList = new ArrayList<>();
                for (WebElement element : driver.findElements(By.className("_3aXen"))) {
                    if(element.getText().equals("스타일리스트")){
                        element.click();
                        Thread.sleep(1000);
                        break;
                    }
                }

                List<WebElement> styleList = driver.findElements(By.className("_3ctAm"));

                for (WebElement element : styleList) {
                    nameList.add(element.getText());
                }

                shopInfo.setName(nameList.stream().map(n -> String.valueOf(n)).collect(Collectors.joining(",")));
            }catch (Exception e){
                shopInfo.setName("");
            }

            driver.switchTo().defaultContent();
            Thread.sleep(1000);
            driver.switchTo().frame(driver.findElement(By.id("searchIframe")));
            Thread.sleep(1000);

            barbershopList.add(shopInfo);
        }


        System.out.println(barbershopList);

        driver.quit();
    }
}
