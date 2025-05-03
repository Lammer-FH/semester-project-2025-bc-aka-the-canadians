all: images pdf

images:
	plantuml ./*.plantuml

pdf:
	magick *.png DB_Design.pdf

clean:
	rm *.png *.pdf
