Mars Rover
==========

## Problem 

> You're working late one night at NASA and you get a signal that the Mars rover
> has seen something extremely important! The rover captured a picture of the
> object, and given the urgent flag on the message you know you need to download
> the image as soon as possible.
> 
> The signal you received contains some metadata about how the image is laid out
> in the rover's memory, and as you review it it seems that some mistake has
> been made. Instead of a single contiguous image, the file has been broken into
> _chunks_ and scattered around the memory banks! Worse, some of the chunks
> overlap and others seem to be duplicates. You can ask the rover to send you
> the data for a given chunk by id.
> 
> The radio link to the rover is very low bandwidth, so you know you need to
> keep costs down by fetching the smallest set of chunks you need to reconstruct
> the image. Write a function which accepts the chunk metadata and returns a list
> of chunk ids to download which cover the entire image in the smallest total
> byte size.


## Problem Input

You are given two pieces of information as inputs:

- `total-size` - the total number of bytes in the image
- `list<Chunk>` - a list of data chunks in the rover's memory

Each `Chunk` record has an opaque `id`, a `start` byte position, and a `size` in
bytes.

There are some small test sets in the test/ directory you can use to test your solution. 
Each problem.tsv has the value of `total-size` on the first line, followed by tab-separated value
lines with the `id`, `start`, and `size` of each chunk of the file.  The solution is provided as solution.txt.

You may wish to create your own.

### Expected Output

The program should take any problem.tsv provided (or others of that format) and print the solution to STDOUT as a list of chunk ids to
download, sorted by id. This can be compared directly to the associated
`solution.txt` in the sample directory.  If correct, the "diff" should be empty.
