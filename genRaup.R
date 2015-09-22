#! /usr/bin/env Rscript

suppressMessages(library(dplyr,quietly=T))
library(argparser,quietly=T)

p = arg_parser("A R script to generate Raupian spirals") %>%
      add_argument("--filename",  default="raupCyl.csv", help="Output filename") %>%
      add_argument("--theta",  default=4*pi, help="How much curve to generate") %>%
      add_argument("--resolution",  default=0.01, help="Step size through theta") %>%
      add_argument("--W",  default=3.5, help="Whorl expansion rate") %>%
      add_argument("--T",  default=0,   help="Translation rate")  %>%
      add_argument("--r0", default=1,   help="Initial r for offset coordinate") %>%
      add_argument("--rc", default=1,   help="Initial r for center") %>%
      add_argument("--y0", default=1,   help="Initial y")  

args = parse_args(p, commandArgs(trailingOnly = TRUE))

if(args$help){
  print(p)
  quit("no")
}

w  = args$W
t  = args$T
r0 = args$r0
y0 = args$y0
rc = args$rc

r <- function(theta, r0, w){
  r0*w^(theta/(2*pi))
}

y <- function(theta, y0, rc, w, t){
  y0*w^(theta/(2*pi)) + rc*t*(w^(theta/(2*pi)) - 1)  
}

points = data.frame(theta = seq(0,args$theta,args$resolution)) %>%
  mutate(r = r(theta,r0,w),
         y = y(theta,y0,rc,w,t),
         a = r*sin(theta),
         b = r*cos(theta),
         c = y)

#points %>%
#  select(a,b,c) %>%
#  write.table(file="raupCart.csv",row.names = F, col.names = F, sep=" ")

points %>%
  select(theta,r,y) %>%
  write.table(file=args$filename,row.names = F, col.names = F, sep=" ")

