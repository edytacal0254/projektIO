# projektIO

### Przygotowanie Środowiska:


  - Pobierz i zainstaluj jdk-15.0.1_windows-x64_bin.exe z https://www.oracle.com/pl/java/technologies/javase-jdk15-downloads.html

  - Pobierz i zainstaluj opencv-4.5.0 z https://sourceforge.net/projects/opencvlibrary/files/4.5.0/opencv-4.5.0-vc14_vc15.exe/download

  - Dalsza część instrukcji na podstawie IntelliJ  
  (dokładniej na przykładzie wersji 2020.3, na innych wersjach proces będzie zbliżony;  
  X na początku ścieżek oznacza lokalizację folderu w którym zainstalowano bibliotekę OpenCV)
      1. Ustaw wersję Javy na 15.0.1
      2. W File > Project Structure > Project Settings > Modules > Dependencies  ORAZ File > Project Structure > Project Settings > Libraries usunąć ścieżki które zostały automatycznie dodane przy pobieraniu repozytorium (powinny być ich 3 i powinny być zaznaczone na czerwono)
      3. W File > Project Structure > Project Settings > Modules > Dependencies dodać __X\opencv\build\java\opencv-450.jar__ (kliknąć “+” a następnie “Add Jar/Directory”)
      4. W File > Project Structure > Project Settings > Libraries dodać __X\opencv\build\bin__  i  __X\opencv\build\java\x64__ (kliknąć “+”)

### Przed uruchomieniem programu Analizatora Monitoringu przetestować działanie biblioteki OpenCV poprzez uruchomienie opencvTest)
