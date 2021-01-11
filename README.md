# projektIO

#### Przygotowanie środowiska:
  - Pobierz i zainstaluj jdk-15.0.1_windows-x64_bin.exe z https://www.oracle.com/pl/java/technologies/javase-jdk15-downloads.html

  - Pobierz i zainstaluj opencv-4.5.0 z https://sourceforge.net/projects/opencvlibrary/files/4.5.0/opencv-4.5.0-vc14_vc15.exe/download

  - Dalsza część instrukcji na podstawie IntelliJ  
  (dokładniej na przykładzie wersji 2020.3, na innych wersjach proces będzie zbliżony;  
  X na początku ścieżek oznacza lokalizację folderu w którym zainstalowano bibliotekę OpenCV)
      1. Ustaw wersję Javy na 15.0.1
      2. W File > Project Structure > Project Settings > Modules > Dependencies  ORAZ File > Project Structure > Project Settings > Libraries usunąć ścieżki które zostały automatycznie dodane przy pobieraniu repozytorium (powinno być ich 3 i powinny być zaznaczone na czerwono)
      3. W File > Project Structure > Project Settings > Modules > Dependencies dodać __X\opencv\build\java\opencv-450.jar__ (kliknąć “+” a następnie “Add Jar/Directory”)
      4. W File > Project Structure > Project Settings > Libraries dodać __X\opencv\build\bin__  i  __X\opencv\build\java\x64__ (kliknąć “+”)

#### Gotowe Środowisko:
  - Pobierz obraz sytemu z https://drive.google.com/file/d/19UVyvzQw3MbCRPRfhAMhD2SvSRREHbLr/view?usp=sharing
  - Uruchom program VirtualBox, w Plik > Importuj Urządzenie Wirtualne wybierz pobrany wcześniej obraz systemu
  - Hasło do obrazu systemu: haslo
  ###### UWAGA: Jest to obraz gotowego środowiska w systemie Windows10 więc zajmuje prawie 10GB 

#### Przed uruchomieniem programu Analizatora Monitoringu należy przetestować działanie biblioteki OpenCV poprzez uruchomienie opencvTest
