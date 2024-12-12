package Data.WebScraping;

import Data.Movie;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class MovieScrapper {

    private final String baseUrl;

    public MovieScrapper(String driverPath, String baseUrl) {
        this.baseUrl = baseUrl;
        System.setProperty("webdriver.chrome.driver", driverPath);
    }

    public List<Movie> getSchedule(String ciudad) {
        List<Movie> movies = new ArrayList<>();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(baseUrl + ciudad);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("article.now__movie")));

            List<WebElement> elementosPeliculas = driver.findElements(By.cssSelector("article.now__movie"));

            for (WebElement elemento : elementosPeliculas) {
                String titulo = elemento.findElement(By.tagName("h3")).getText();
                String clasificacion = elemento.findElement(By.className("clasificacion")).getText();
                String imagenUrl = elemento.findElement(By.tagName("img")).getAttribute("src");
                if (titulo != "") movies.add(new Movie(titulo, clasificacion, imagenUrl));
            }

        } finally {
            driver.quit();
        }
        return movies;
    }
}
