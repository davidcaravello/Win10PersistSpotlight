import static groovy.io.FileType.FILES
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING

import javax.imageio.ImageIO
import javax.imageio.stream.FileImageInputStream
import java.nio.file.*

IMAGE_MIN_WIDTH=1920
IMAGE_MIN_HEIGHT=1080
imageReader = ImageIO.getImageReadersByFormatName('jpeg').next(); // unsafe, may fail if no readers found

def userHomeDir = System.properties['user.home']

def imageInputPath = Paths.get(userHomeDir + "\\AppData\\Local\\Packages\\Microsoft.Windows.ContentDeliveryManager_cw5n1h2txyewy\\LocalState\\Assets")
def imageOutputPath = Paths.get(userHomeDir + "\\Pictures\\SpotlightImages")

def dimensions(File file)
{
    try
    {
        def fileInputStream = new FileImageInputStream(file)
        imageReader.setInput(fileInputStream)
        return [imageReader.getWidth(0),  imageReader.getHeight(0)]
    }
    catch(e) { }

    return [0, 0];
}

Files.createDirectories(imageOutputPath)

imageInputPath.eachFile(FILES, {
    (width, height)=dimensions(it.toFile());

    if(width < IMAGE_MIN_WIDTH || height < IMAGE_MIN_HEIGHT)
        return

    def destinationPath = Paths.get(imageOutputPath.toString(), it.getFileName().toString() + ".jpg")

    if(Files.exists(destinationPath))
        return

    Files.copy(it, destinationPath, REPLACE_EXISTING)
    println "Copied: ${destinationPath}"
})

println "Done"
